/**
 * 
 */
package br.com.cams7.app.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import br.com.cams7.app.dao.BaseDAO;
import br.com.cams7.app.model.AbstractEntity;
import br.com.cams7.app.utils.AppInvalidDataException;
import br.com.cams7.app.utils.SearchParams;

/**
 * @author César Magalhães
 *
 * @param <PK>
 *            ID da entidade
 * @param <E>
 *            Entidade
 * @param <DAO>
 *            Data Access Object
 */
@Transactional
public abstract class AbstractService<PK extends Serializable, E extends AbstractEntity<PK>, DAO extends BaseDAO<PK, E>>
		implements BaseService<PK, E> {

	protected final Logger LOGGER;

	@Autowired
	private DAO dao;

	private String username;

	public AbstractService() {
		super();

		LOGGER = LoggerFactory.getLogger(this.getClass());
	}

	protected DAO getDao() {
		return dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#getById(java.io.Serializable)
	 */
	@Transactional(readOnly = true)
	@Override
	public E getById(PK id) {
		E entity = getDao().getById(id);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#persist(br.com.cams7.app.model.
	 * AbstractEntity)
	 */
	@Override
	public void persist(E entity) {
		getDao().persist(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#update(br.com.cams7.app.model.
	 * AbstractEntity)
	 */
	@Override
	public void update(E entity) {
		getDao().update(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#delete(java.io.Serializable)
	 */
	@Override
	public void delete(PK id) {
		getDao().delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#getAll()
	 */
	@Transactional(readOnly = true)
	@Override
	public List<E> getAll() {
		List<E> entities = getDao().getAll();
		return entities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.dao.BaseDAO#search(br.com.cams7.app.utils.SearchParams)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<E> search(SearchParams params) {
		List<E> entities = getDao().search(params);
		return entities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#getTotalElements(java.util.Map,
	 * java.lang.String[])
	 */
	@Transactional(readOnly = true)
	@Override
	public long getTotalElements(Map<String, Object> filters, String... globalFilters) {
		long total = getDao().getTotalElements(filters, globalFilters);
		return total;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#count()
	 */
	@Transactional(readOnly = true)
	@Override
	public long count() {
		long count = getDao().count();
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.dao.BaseDAO#setIgnoredJoins(java.lang.Class[])
	 */
	@Override
	public void setIgnoredJoins(@SuppressWarnings("unchecked") Class<? extends AbstractEntity<?>>... ignoredJoins) {
		getDao().setIgnoredJoins(ignoredJoins);
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the username
	 */
	protected String getUsername() {
		return username;
	}

	/**
	 * Varifica se o id foi informado
	 * 
	 * @param id
	 *            ID da entidade
	 */
	protected final void verificaId(Serializable id) {
		if (id == null)
			throw new AppInvalidDataException("O id não foi informado...");
	}

}
