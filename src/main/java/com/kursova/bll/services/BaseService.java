package com.kursova.bll.services;

import java.util.List;

/**
 * Base service interface providing common CRUD operations
 * @param <T> DTO type
 * @param <ID> Primary key type
 */
public interface BaseService<T, ID> {

    /**
     * Create new entity
     */
    T create(T dto);

    /**
     * Find entity by ID
     */
    T findById(ID id);

    /**
     * Find all entities
     */
    List<T> findAll();

    /**
     * Update existing entity
     */
    T update(ID id, T dto);

    /**
     * Delete entity by ID
     */
    void delete(ID id);

    /**
     * Check if entity exists by ID
     */
    boolean existsById(ID id);
}
