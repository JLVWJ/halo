package com.lvwj.halo.mybatisplus.plugins;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.lvwj.halo.mybatisplus.config.prop.MybatisPlusExtProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.util.List;

/**
 * 扩展BlockAttackInnerInterceptor，支持全表SELECT sql语句拦截
 *
 * @author lvweijie
 * @date 2023年12月06日 17:18
 */
public class MyBlockAttackInnerInterceptor extends BlockAttackInnerInterceptor {

    @Autowired
    private MybatisPlusExtProperties mybatisPlusExtProperties;

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler handler = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = handler.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE || sct == SqlCommandType.SELECT) {
            if (InterceptorIgnoreHelper.willIgnoreBlockAttack(ms.getId())) return;
            BoundSql boundSql = handler.boundSql();
            parserMulti(boundSql.getSql(), null);
        }
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        List<String> ignoreTables = mybatisPlusExtProperties.getBlockAttack().getIgnoreTables();
        //表在忽略校验配置内，不做where条件校验
        if (CollectionUtils.isNotEmpty(ignoreTables) && ignoreTables.contains(update.getTable().getName())) {
            return;
        }
        super.processUpdate(update, index, sql, obj);
    }

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        List<String> ignoreTables = mybatisPlusExtProperties.getBlockAttack().getIgnoreTables();
        //表在忽略校验配置内，不做where条件校验
        if (CollectionUtils.isNotEmpty(ignoreTables) && ignoreTables.contains(delete.getTable().getName())) {
            return;
        }
        super.processDelete(delete, index, sql, obj);
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        PlainSelect plainSelect = (PlainSelect) select;
        if (!(plainSelect.getFromItem() instanceof Table)) {
            return;
        }
        Table table = (Table) plainSelect.getFromItem();
        List<String> ignoreTables = mybatisPlusExtProperties.getBlockAttack().getIgnoreTables();
        //表在忽略配置内时，不做where条件校验
        if (CollectionUtils.isNotEmpty(ignoreTables) && ignoreTables.contains(table.getName())) {
            return;
        }
        //存在且只有count()时，不做where条件校验。
        if (plainSelect.getSelectItems().stream().anyMatch(this::isSelectCount)) {
            return;
        }
        //存在limit时，不做where条件校验
        if (null != plainSelect.getLimit()) {
            return;
        }
        Expression where = plainSelect.getWhere();
        this.checkWhere(table.getName(), where, "检测到异常SQL，已被拦截，非法SQL:" + sql);
    }

    /**
     * 判断select sql是否存在count(*)
     *
     * @param s SelectItem
     * @return boolean
     * @author lvweijie
     * @date 2023/12/6 18:13
     */
    private boolean isSelectCount(SelectItem<?> s) {
        Expression expression = s.getExpression();
        return expression.toString().toLowerCase().replaceAll(" ", "").startsWith("count(");
    }
}
