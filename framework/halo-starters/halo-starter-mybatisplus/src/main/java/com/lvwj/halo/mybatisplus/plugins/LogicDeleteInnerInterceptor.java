package com.lvwj.halo.mybatisplus.plugins;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.ThreadLocalUtil;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Connection;
import java.time.LocalDateTime;

/**
 * 逻辑删除拦截器：补充逻辑删除其他信息(删除时间、删除人等)
 *
 * @author lvweijie
 * @date 2023年12月06日 17:18
 */
public class LogicDeleteInnerInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private static final String IS_DELETE = "is_delete";
    private static final String DELETE_TIME = "delete_time";
    private static final String DELETE_BY = "delete_by";


    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler handler = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = handler.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.UPDATE) {
            BoundSql boundSql = handler.boundSql();
            String sql = parserMulti(boundSql.getSql(), null);
            if (Func.isNotBlank(sql) && !sql.equals(boundSql.getSql())) {
                PluginUtils.mpBoundSql(boundSql).sql(sql);
            }
        }
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        String tableName = update.getTable().getName();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        //开启了逻辑删除 且 update sql是逻辑删除sql
        if (tableInfo.isWithLogicDelete()
                && update.getUpdateSets().stream().anyMatch(s -> s.getColumns().get(0).getColumnName().equals(IS_DELETE))) {
            if (tableInfo.getFieldList().stream().anyMatch(s -> s.getColumn().equals(DELETE_TIME))) {
                update.addUpdateSet(new UpdateSet(new Column(DELETE_TIME), new StringValue(DateTimeUtil.formatDateTime(LocalDateTime.now()))));
            }
            if (tableInfo.getFieldList().stream().anyMatch(s -> s.getColumn().equals(DELETE_BY))) {
                update.addUpdateSet(new UpdateSet(new Column(DELETE_BY), new StringValue(ThreadLocalUtil.getCurrentUser())));
            }
        }
    }
}