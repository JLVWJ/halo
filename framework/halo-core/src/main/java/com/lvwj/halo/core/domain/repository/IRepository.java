package com.lvwj.halo.core.domain.repository;

import com.lvwj.halo.core.domain.entity.IAggregate;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 仓储接口
 */
public interface IRepository<T extends IAggregate<ID>, ID extends Serializable> {

  Optional<T> find(ID id, boolean track, boolean join);

  default Optional<T> find(ID id) {
    return find(id, true, true);
  }

  default Optional<T> find(ID id, boolean track) {
    return find(id, track, true);
  }

  List<T> findList(List<ID> ids, boolean track, boolean join);

  default List<T> findList(List<ID> ids) {
    return findList(ids, true, true);
  }

  default List<T> findList(List<ID> ids, boolean track) {
    return findList(ids, track, true);
  }

  List<T> findAll(boolean track, boolean join);

  default List<T> findAll() {
    return findAll(true, true);
  }

  default List<T> findAll(boolean track) {
    return findAll(track, true);
  }

  void save(T entity);

  void save(List<T> entities);

  void remove(T entity);

  void remove(List<T> entities);
}
