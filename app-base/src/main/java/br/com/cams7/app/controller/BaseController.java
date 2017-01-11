/**
 * 
 */
package br.com.cams7.app.controller;

import static org.springframework.http.HttpStatus.OK;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.cams7.app.model.AbstractEntity;

/**
 * @author César Magalhães
 *
 */
/**
 * @author César Magalhães
 *
 * @param <E>
 * @param <PK>
 */
public interface BaseController<E extends AbstractEntity<PK>, PK extends Serializable> {
	/**
	 * Display a listing of the resource.
	 *
	 * @param model
	 * @return
	 */
	@GetMapping
	@ResponseStatus(OK)
	String index(ModelMap model);

	/**
	 * Show the form for creating a new resource.
	 *
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/create")
	@ResponseStatus(OK)
	String create(ModelMap model);

	/**
	 * Store a newly created resource in storage.
	 *
	 * @param entity
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/create")
	String store(E entity, BindingResult result, ModelMap model, Integer lastLoadedPage);

	/**
	 * Display the specified resource.
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(OK)
	String show(PK id, ModelMap model);

	/**
	 * Show the form for editing the specified resource.
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/{id}/edit")
	@ResponseStatus(OK)
	String edit(PK id, ModelMap model);

	/**
	 * Update the specified resource in storage.
	 *
	 * @param entity
	 * @param id
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/{id}/edit")
	String update(E entity, BindingResult result, ModelMap model, PK id, Integer lastLoadedPage);

	/**
	 * Remove the specified resource from storage.
	 *
	 * @param id
	 * @return
	 */
	@PostMapping(value = "/{id}/delete")
	ResponseEntity<String> destroy(PK id);

	/**
	 * @param model
	 * @param offset
	 * @param sortField
	 * @param sortOrder
	 * @param query
	 * @return
	 */
	@GetMapping(value = "/list")
	@ResponseStatus(OK)
	String list(ModelMap model, Integer offset, String sortField, String sortOrder, String query);
}
