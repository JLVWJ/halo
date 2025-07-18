package com.lvwj.halo.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lvwj.halo.common.models.entity.IEntity;
import com.lvwj.halo.common.utils.CharPool;
import com.lvwj.halo.common.utils.Func;
import com.lvwj.halo.common.utils.StringUtil;
import com.lvwj.halo.mybatisplus.annotation.JoinEntity;
import com.lvwj.halo.mybatisplus.mapper.CustomMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 数据实体容器
 *
 * @author lvwj
 * @date 2022-12-06 14:20
 */
@Slf4j
public class EntityHolder {

  /**
   * entity class - mapper
   */
  private static final Map<Class<?>, CustomMapper<? extends IEntity<?>>> MapperMap = new HashMap<>();
  /**
   * entity class - fields
   */
  private static final Map<Class<?>, List<EntityField>> FieldMap = new HashMap<>();

  @Autowired(required = false)
  private List<CustomMapper<? extends IEntity<?>>> mappers;

  @PostConstruct
  public void init() {
    if (CollectionUtils.isEmpty(mappers)) {
      return;
    }
    for (CustomMapper<? extends IEntity<?>> mapper : mappers) {
      Class<?> cls = getEntityClass(mapper);
      TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
      if (null == tableInfo) {
        continue;
      }

      MapperMap.put(cls, mapper);

      Map<Field, TableFieldInfo> fieldInfoMap = tableInfo.getFieldList().stream().collect(Collectors.toMap(TableFieldInfo::getField, Function.identity()));
      FieldMap.put(cls, ReflectionKit.getFieldList(cls).stream().map(s -> new EntityField(s, fieldInfoMap.get(s))).collect(Collectors.toList()));
    }
  }

  /**
   * 根据数据实体类型获取指定Mapper
   *
   * @param clazz 数据实体类型
   * @author lvwj
   * @date 2022-12-06 14:45
   */
  public static CustomMapper getMapper(Class<?> clazz) {
    String errorMsg = String.format("PO[%s] has no mapper which extends CustomMapper!", clazz.getName());
    return Optional.ofNullable(MapperMap.get(clazz)).orElseThrow(() -> new RuntimeException(errorMsg));
  }

  public static CustomMapper getMapper(String className) {
    return getMapper(getEntityClass(className));
  }

  public static Class<?> getEntityClass(String entityClassName) {
    Class<?> clazz;
    try {
      clazz = Class.forName(entityClassName);
      return clazz;
    } catch (ClassNotFoundException e) {
      log.error("entityClassName convert to entityClass failed!", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 获取数据实体指定字段的值
   *
   * @author lvwj
   * @date 2022-12-10 18:24
   */
  public static Object getFieldValue(Object entity, String fieldName) {
    EntityField entityField = getEntityField(entity.getClass(), fieldName);
    if (null == entityField) {
      return null;
    }
    return entityField.getFieldValue(entity);
  }

  public static void setFieldValue(Object entity, String fieldName, Object value) {
    EntityField entityField = getEntityField(entity.getClass(), fieldName);
    if (null == entityField) {
      return;
    }
    entityField.setFieldValue(entity, value);
  }

  /**
   * 获取实体字段对应的表列名
   *
   * @author lvwj
   * @date 2022-12-12 15:50
   */
  public static String getColumnName(Class<?> entityClass, String fieldName) {
    EntityField entityField = getEntityField(entityClass, fieldName);
    if (null == entityField) {
      return null;
    }
    return Optional.ofNullable(entityField.getFieldInfo()).map(TableFieldInfo::getColumn).orElseThrow(
            () -> new RuntimeException(
                    String.format("entity[%s]-field[%s] has no column", entityClass.getName(), fieldName)));
  }

  /**
   * 获取数据实体指定字段
   *
   * @author lvwj
   * @date 2022-12-10 18:24
   */
  public static EntityField getEntityField(Class<?> entityClass, String fieldName) {
    if (!StringUtils.hasLength(fieldName)) {
      return null;
    }
    return getEntityFields(entityClass).stream().filter(s -> s.matchName(fieldName)).findFirst().orElse(null);
  }

  public static List<EntityField> getEntityFields(Class<?> entityClass) {
    return FieldMap.getOrDefault(entityClass, new ArrayList<>());
  }

  public static List<EntityField> getEntityJoinFields(Class<?> entityClass) {
    return getEntityFields(entityClass).stream().filter(s -> null != s.getJoinEntity()).collect(Collectors.toList());
  }

  /**
   * 判断实体是否存在版本号字段
   *
   * @author lvwj
   * @date 2022-12-25 19:56
   */
  public static boolean hasVersionField(Class<?> entityClass) {
    return FieldMap.getOrDefault(entityClass, new ArrayList<>()).stream().anyMatch(s -> s.matchName(VERSION));
  }

  /**
   * 递归关联数据实体
   *
   * @author lvwj
   * @date 2022-12-25 19:56
   */
  public static void joinEntity(Class<?> clazz, List<IEntity<?>> entities) {
    //获取数据实体下有加@JoinEntity的字段
    List<EntityField> fieldList = getEntityJoinFields(clazz);
    if (CollectionUtils.isEmpty(fieldList)) {
      return;
    }

    //循环处理加@JoinEntity的字段
    for (EntityField entityField : fieldList) {
      Class<?> fieldActualType = entityField.getFieldActualType();//字段实际类型
      String primaryKey = entityField.getJoinEntity().primaryKey();
      String foreignKey = entityField.getJoinEntity().foreignKey();
      String extraCondition = parseExtraCondition(fieldActualType, entityField.getJoinEntity().extraCondition());

      //获取primaryKey集合
      Set<Object> pks = entities.stream().map(entityField::getPrimaryKeyValue).filter(Func::isNotEmpty).collect(Collectors.toSet());
      if (Func.isEmpty(pks)) continue;
      QueryWrapper<IEntity<?>> query = Wrappers.query();
      if (pks.size() > 1) {
        query.in(StringUtils.hasLength(primaryKey), "id", pks);
        query.in(StringUtils.hasLength(foreignKey), getColumnName(fieldActualType, foreignKey), pks);
      } else {
        query.eq(StringUtils.hasLength(primaryKey), "id", pks.iterator().next());
        query.eq(StringUtils.hasLength(foreignKey), getColumnName(fieldActualType, foreignKey), pks.iterator().next());
      }
      if (Func.isNotEmpty(extraCondition)) {
        query.last(extraCondition);
      }
      //根据fieldActualType定位到对应mapper接口，获取关联数据集合
      List<IEntity<?>> list = getMapper(fieldActualType).selectList(query);
      if (CollectionUtils.isEmpty(list)) {
        continue;
      }

      //注解@JoinEntity的primaryKey有值，说明当前实体和关联实体是一对一关系 或 多对一关系
      if (StringUtils.hasLength(primaryKey)) {
        Map<Object, IEntity<?>> subMap = list.stream().collect(Collectors.toMap(IEntity::getId, Function.identity(), (o, n) -> n));
        for (IEntity<?> entity : entities) {
          Object primaryKeyValue = entityField.getPrimaryKeyValue(entity);
          if (null != primaryKeyValue) {
            entityField.setFieldValue(entity, subMap.get(primaryKeyValue));
          }
        }
      }

      //注解@JoinEntity的foreignKey有值，说明当前实体和关联实体是一对一关系 或 一对多关系
      if (StringUtils.hasLength(foreignKey)) {
        Map<Object, List<IEntity<?>>> subMap = list.stream().collect(groupingBy(s -> getFieldValue(s, foreignKey)));
        for (IEntity<?> entity : entities) {
          Object fieldValue = getFieldValue(entity, foreignKey);
          Object fk = foreignKey.equals("id") || null == fieldValue ? entity.getId() : fieldValue;
          if (entityField.isListType()) {
            entityField.setFieldValue(entity, subMap.get(fk));
          } else if (entityField.isSetType()) {
            Set<Object> set = new HashSet<>(subMap.get(fk));
            entityField.setFieldValue(entity, set);
          } else {
            List<IEntity<?>> subList = subMap.get(fk);
            entityField.setFieldValue(entity, subList.isEmpty() ? null : subList.get(0));
          }
        }
      }

      //递归加载关联数据
      joinEntity(fieldActualType, list);
    }
  }

  private static String parseExtraCondition(Class<?> fieldActualType, String extraCondition) {
    if (Func.isEmpty(extraCondition)) {
      return null;
    }
    Map<String, String> fieldNameMap = new HashMap<>();
    List<Character> ignore = Arrays.asList(CharPool.EQUAL_TO, CharPool.LEFT_BRACKET, CharPool.RIGHT_BRACKET, CharPool.SPACE, CharPool.QUOTE, CharPool.SINGLE_QUOTE, CharPool.NEWLINE);
    List<Character> chars = new ArrayList<>();
    extraCondition.chars().forEach(c -> {
      char ch = (char) c;
      if (!ignore.contains(ch)) {
        chars.add(ch);
      } else {
        if (!chars.isEmpty()) {
          String fieldName = String.valueOf(chars);
          String columnName = getColumnName(fieldActualType, fieldName);
          if (Func.isNotEmpty(columnName)) {
            fieldNameMap.put(fieldName, getColumnName(fieldActualType, fieldName));
          }
          //fieldName.indexOf(CharPool.UNDERSCORE) > -1 表示用的可能是表字段名
          else if (fieldName.indexOf(CharPool.UNDERSCORE) > -1) {
            if (Func.isNotEmpty(getColumnName(fieldActualType, StringUtil.underlineToHump(fieldName)))) {
              fieldNameMap.put(fieldName, fieldName);
            }
          }
          chars.clear();
        }
      }
    });
    String result = extraCondition;
    for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
      if (!entry.getKey().equals(entry.getValue())) {
        result = result.replace(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  private Class<?> getEntityClass(CustomMapper<?> mapper) {
    return ReflectionKit.getSuperClassGenericType(mapper.getClass(), BaseMapper.class, 0);
  }

  /**
   * 数据实体字段
   *
   * @author lvwj
   * @date 2022-12-10 18:24
   */
  @Getter
  public static class EntityField {

    private final Field field;
    private final Class<?> fieldType;
    private final Class<?> fieldActualType;
    private final JoinEntity joinEntity;
    private final TableFieldInfo fieldInfo;

    public EntityField(Field field, TableFieldInfo fieldInfo) {
      this.field = field;
      this.fieldType = field.getType();
      this.fieldActualType = fieldActualType();
      this.fieldInfo = fieldInfo;
      this.joinEntity = field.getAnnotation(JoinEntity.class);
      checkJoinEntity();
    }

    private void checkJoinEntity() {
      if (null == this.joinEntity) {
        return;
      }
      //@JoinEntity 不支持的字段类型
      if (ClassUtils.isPrimitiveOrWrapper(fieldType) || fieldType.isEnum() || fieldType.isArray() || Map.class.isAssignableFrom(fieldType)) {
        throw new RuntimeException(String.format("Field[%s]: @JoinEntity isn't support type[%s]  ", getFieldName(), getFieldTypeName()));
      }

      //@JoinEntity primaryKey或foreignKey 是否设置正确
      String primaryKey = joinEntity.primaryKey();
      String foreignKey = joinEntity.foreignKey();
      if (!StringUtils.hasLength(primaryKey) && !StringUtils.hasLength(foreignKey)
              || (StringUtils.hasLength(primaryKey) && StringUtils.hasLength(foreignKey))) {
        throw new RuntimeException(
                String.format("Field[%s]: @JoinEntity's foreignKey or primaryKey only one can be set", getFieldName()));
      }
      if (StringUtils.hasLength(primaryKey) && isCollectionType()) {
        throw new RuntimeException(
                String.format("Field[%s]: @JoinEntity primaryKey isn't applicable to Type[Collection]", getFieldName()));
      }
    }

    public Object getPrimaryKeyValue(IEntity<?> entity) {
      Object primaryKeyValue = entity.getId();
      if (null != joinEntity) {
        String primaryKey = joinEntity.primaryKey();
        if (StringUtils.hasLength(primaryKey) && !primaryKey.equals("id")) {
          primaryKeyValue = EntityHolder.getFieldValue(entity, primaryKey);
        }
      }
      return primaryKeyValue;
    }

    public boolean matchName(String fieldName) {
      return this.getFieldName().equals(fieldName);
    }

    public boolean allowUpdate(Object value) {
      List<FieldStrategy> notNull = Arrays.asList(FieldStrategy.NOT_NULL, FieldStrategy.DEFAULT);
      return null != fieldInfo &&
              (fieldInfo.getUpdateStrategy() == FieldStrategy.IGNORED
                      || fieldInfo.getUpdateStrategy() == FieldStrategy.ALWAYS
                      || fieldInfo.getUpdateStrategy() == FieldStrategy.NOT_EMPTY && !ObjectUtils.isEmpty(value)
                      || notNull.contains(fieldInfo.getUpdateStrategy()) && null != value);
    }

    public boolean isCollectionType() {
      return Collection.class.isAssignableFrom(this.fieldType);
    }

    public boolean isListType() {
      return List.class.isAssignableFrom(this.fieldType);
    }

    public boolean isSetType() {
      return Set.class.isAssignableFrom(this.fieldType);
    }

    public void setFieldValue(Object entity, Object value) {
      ReflectionUtils.makeAccessible(this.field);
      ReflectionUtils.setField(this.field, entity, value);
    }

    public Object getFieldValue(Object entity) {
      ReflectionUtils.makeAccessible(this.field);
      return ReflectionUtils.getField(this.field, entity);
    }

    public String getFieldName() {
      return this.field.getName();
    }

    public String getFieldTypeName() {
      return this.fieldType.getName();
    }

    /**
     * 获取字段的实际类型，集合类型则取集合的泛型类型
     *
     * @author lvwj
     * @date 2022-12-12 15:33
     */
    private Class<?> fieldActualType() {
      Class<?> fieldActualType = this.fieldType;
      if (isCollectionType()) {
        Type genericType = this.field.getGenericType();
        if (genericType instanceof ParameterizedType) {
          //得到泛型里的class类型对象
          fieldActualType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
      }
      return fieldActualType;
    }
  }

  public static final String VERSION = "version";
}
