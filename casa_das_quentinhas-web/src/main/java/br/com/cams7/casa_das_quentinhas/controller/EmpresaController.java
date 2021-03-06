/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.controller;

import static br.com.cams7.casa_das_quentinhas.entity.Empresa.RelacionamentoEmpresa.FUNCIONARIOS;
import static br.com.cams7.casa_das_quentinhas.entity.Empresa.RelacionamentoEmpresa.PEDIDOS;
import static br.com.cams7.casa_das_quentinhas.entity.Empresa.Tipo.CLIENTE;
import static br.com.cams7.casa_das_quentinhas.entity.Empresa.Tipo.ENTREGA;
import static org.springframework.http.HttpStatus.OK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import br.com.cams7.app.SearchParams;
import br.com.cams7.app.SearchParams.SortOrder;
import br.com.cams7.app.controller.AbstractBeanController;
import br.com.cams7.casa_das_quentinhas.entity.Cidade;
import br.com.cams7.casa_das_quentinhas.entity.Cliente;
import br.com.cams7.casa_das_quentinhas.entity.Contato;
import br.com.cams7.casa_das_quentinhas.entity.Empresa;
import br.com.cams7.casa_das_quentinhas.entity.Empresa.RegimeTributario;
import br.com.cams7.casa_das_quentinhas.entity.Empresa.Tipo;
import br.com.cams7.casa_das_quentinhas.entity.Endereco;
import br.com.cams7.casa_das_quentinhas.entity.Funcionario;
import br.com.cams7.casa_das_quentinhas.entity.Funcionario.Funcao;
import br.com.cams7.casa_das_quentinhas.entity.Pedido;
import br.com.cams7.casa_das_quentinhas.entity.Usuario;
import br.com.cams7.casa_das_quentinhas.service.CidadeService;
import br.com.cams7.casa_das_quentinhas.service.EmpresaService;
import br.com.cams7.casa_das_quentinhas.service.FuncionarioService;
import br.com.cams7.casa_das_quentinhas.service.PedidoService;

/**
 * @author C??sar Magalh??es
 *
 */
@Controller
@RequestMapping("/" + EmpresaController.MODEL_NAME)
@SessionAttributes({ "empresaTipos", "empresaRegimesTributarios" })
public class EmpresaController extends AbstractBeanController<Integer, Empresa, EmpresaService> {

	public static final String MODEL_NAME = "empresa";
	public static final String LIST_NAME = "empresas";

	@Autowired
	private CidadeService cidadeService;

	@Autowired
	private FuncionarioService entregadorService;

	@Autowired
	private PedidoService pedidoService;

	private final short MAX_RESULTS = 5;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractBeanController#store(br.com.cams7.app
	 * .entity.AbstractEntity, org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String store(@Valid Empresa empresa, BindingResult result, ModelMap model, HttpServletRequest request) {
		setPreviousPageAtribute(model, request);
		setCommonAttributes(model);
		// incrementPreviousPage(model, request);

		Usuario usuario = empresa.getUsuarioAcesso();
		Cidade cidade = empresa.getCidade();

		// 1?? valida????o
		if (cidade.getId() == null) {
			FieldError cidadeError = new FieldError(getModelName(), "cidade.id",
					getMessageSource().getMessage("NotNull.empresa.cidade.id", null, LOCALE));
			result.addError(cidadeError);
		}

		if (usuario.getSenha().isEmpty()) {
			FieldError senhaError = new FieldError(getModelName(), "usuarioAcesso.senha",
					getMessageSource().getMessage("NotEmpty.empresa.usuarioAcesso.senha", null, LOCALE));
			result.addError(senhaError);
		}

		setNotEmptyConfirmacaoError(usuario, result, true);

		// 2?? valida????o
		setSenhaNotEqualsConfirmacaoError(usuario, result);
		setEmailNotUniqueError(empresa, result);
		setCNPJNotUniqueError(empresa, result);

		if (result.hasErrors())
			return getCreateTilesPage();

		empresa.setCnpj(empresa.getUnformattedCnpj());
		empresa.getContato().setTelefone(empresa.getContato().getUnformattedTelefone());
		empresa.getEndereco().setCep(empresa.getEndereco().getUnformattedCep());

		getService().setUsername(getUsername());
		getService().persist(empresa);

		sucessMessage(model);
		return redirectToPreviousPage(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.controller.AbstractBeanController#show(java.io.
	 * Serializable, org.springframework.ui.ModelMap,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String show(@PathVariable Integer id, ModelMap model, HttpServletRequest request) {
		Tipo tipo = getService().getEmpresaIipoById(id);

		if (tipo != null)
			switch (tipo) {
			case CLIENTE:
				loadPedidos(id, model, 0, "id", SortOrder.DESCENDING);
				break;
			case ENTREGA:
				loadEntregadores(id, model, 0, "id", SortOrder.DESCENDING);
				break;
			default:
				break;
			}

		return super.show(id, model, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractBeanController#update(br.com.cams7.
	 * app.entity.AbstractEntity, org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, java.io.Serializable,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String update(@Valid Empresa empresa, BindingResult result, ModelMap model, @PathVariable Integer id,
			HttpServletRequest request) {
		setPreviousPageAtribute(model, request);
		setCommonAttributes(model);
		// incrementPreviousPage(model, request);
		setEditPage(model);

		Usuario usuario = empresa.getUsuarioAcesso();

		// 1?? valida????o
		setNotEmptyConfirmacaoError(usuario, result, !usuario.getSenha().isEmpty());

		// 2?? valida????o
		setSenhaNotEqualsConfirmacaoError(usuario, result);
		setEmailNotUniqueError(empresa, result);
		setCNPJNotUniqueError(empresa, result);
		setTipoNotValidError(empresa, result);

		if (result.hasErrors())
			return getCreateTilesPage();

		empresa.setCnpj(empresa.getUnformattedCnpj());
		empresa.getContato().setTelefone(empresa.getContato().getUnformattedTelefone());
		empresa.getEndereco().setCep(empresa.getEndereco().getUnformattedCep());

		getService().update(empresa);

		sucessMessage(model, empresa);
		return redirectToPreviousPage(request);
	}

	/**
	 * Carrega os entregadores da empresa de entregada
	 * 
	 * @param empresaId
	 *            ID da empresa
	 * @param model
	 * @param offset
	 * @param sortField
	 * @param sortOrder
	 * @param query
	 * @return
	 */
	@GetMapping(value = "/{empresaId}/entregadores")
	@ResponseStatus(OK)
	public String entregadores(@PathVariable Integer empresaId, ModelMap model,
			@RequestParam(value = "offset", required = true) Integer offset,
			@RequestParam(value = "f", required = true) String sortField,
			@RequestParam(value = "s", required = true) String sortOrder,
			@RequestParam(value = "q", required = true) String query) {
		loadEntregadores(empresaId, model, offset, sortField, SortOrder.get(sortOrder));

		return "entregador_list";
	}

	/**
	 * Carrega os pedidos da empresa cliente
	 * 
	 * @param empresaId
	 *            ID da empresa
	 * @param model
	 * @param offset
	 * @param sortField
	 * @param sortOrder
	 * @param query
	 * @return
	 */
	@GetMapping(value = "/{empresaId}/pedidos")
	@ResponseStatus(OK)
	public String pedidos(@PathVariable Integer empresaId, ModelMap model,
			@RequestParam(value = "offset", required = true) Integer offset,
			@RequestParam(value = "f", required = true) String sortField,
			@RequestParam(value = "s", required = true) String sortOrder,
			@RequestParam(value = "q", required = true) String query) {
		loadPedidos(empresaId, model, offset, sortField, SortOrder.get(sortOrder));

		return "pedido_list";
	}

	/**
	 * @param nomeOrIbge
	 * @return Na requisi????o AJAX, carrega as cidades
	 */
	@GetMapping(value = "/cidades/{nomeOrIbge}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<Integer, String>> getCidades(@PathVariable String nomeOrIbge) {
		Map<Integer, String> cidades = cidadeService.getCidadesByNomeOrIbge(nomeOrIbge);

		return new ResponseEntity<Map<Integer, String>>(cidades, OK);
	}

	/**
	 * @return Possiveis tipos de empresa
	 */
	@ModelAttribute("empresaTipos")
	public Tipo[] initializeTipos() {
		return Tipo.values();
	}

	/**
	 * @return Possiveis regimes tribut??rios
	 */
	@ModelAttribute("empresaRegimesTributarios")
	public RegimeTributario[] initializeRegimesTributarios() {
		return RegimeTributario.values();
	}

	@Override
	protected String getModelName() {
		return MODEL_NAME;
	}

	@Override
	protected String getListName() {
		return LIST_NAME;
	}

	@Override
	protected String[] getGlobalFilters() {
		return new String[] { "razaoSocial", "cnpj", "contato.email", "contato.telefone", "cidade.nome" };
	}

	@Override
	protected Empresa getNewEntity() {
		Empresa empresa = new Empresa();
		empresa.setCidade(new Cidade());
		empresa.setUsuarioAcesso(new Usuario());
		empresa.setEndereco(new Endereco());
		empresa.setContato(new Contato());

		return empresa;
	}

	@Override
	protected Empresa getEntity(Integer id) {
		Empresa empresa = getService().getEmpresaByIdAndTipos(id, CLIENTE, ENTREGA);
		empresa.setUsuarioAcesso(new Usuario());
		return empresa;
	}

	@Override
	protected Map<String, Object> getFilters() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("tipo", new Tipo[] { CLIENTE, ENTREGA });
		return filters;
	}

	/**
	 * 1?? valida????o
	 * 
	 * Verifica se o campo de confirma????o de senha n??o esta vazio, caso o campo
	 * senha tenha sido preenchido anteriormente
	 * 
	 * @param usuario
	 *            Usu??rio
	 * @param result
	 * @param senhaInformada
	 *            A senha foi informada
	 */
	private void setNotEmptyConfirmacaoError(Usuario usuario, BindingResult result, boolean senhaInformada) {
		if (senhaInformada && usuario.getConfirmacaoSenha().isEmpty()) {
			FieldError confirmacaoError = new FieldError(getModelName(), "usuarioAcesso.confirmacaoSenha",
					getMessageSource().getMessage("NotEmpty.usuario.confirmacaoSenha", null, LOCALE));
			result.addError(confirmacaoError);
		}
	}

	/**
	 * 2?? valida????o
	 * 
	 * Verifica se senha informada ?? mesma do campo confirma????o
	 * 
	 * @param usuario
	 *            Usu??rio
	 * @param result
	 */
	private void setSenhaNotEqualsConfirmacaoError(Usuario usuario, BindingResult result) {
		final String FIELD_NAME = "usuarioAcesso.confirmacaoSenha";
		if (result.getFieldErrorCount(FIELD_NAME) > 0)
			return;

		if (!usuario.getSenha().isEmpty() && !usuario.getConfirmacaoSenha().isEmpty()
				&& !usuario.getSenha().equals(usuario.getConfirmacaoSenha())) {
			FieldError confirmacaoError = new FieldError(getModelName(), FIELD_NAME,
					getMessageSource().getMessage("NotEquals.usuario.confirmacaoSenha", null, LOCALE));
			result.addError(confirmacaoError);
		}
	}

	/**
	 * 2?? valida????o
	 * 
	 * Verifica se o e-mail informado n??o foi cadastrado anteriormente
	 * 
	 * @param empresa
	 * @param result
	 */
	private void setEmailNotUniqueError(Empresa empresa, BindingResult result) {
		final String FIELD_NAME = "contato.email";
		if (result.getFieldErrorCount(FIELD_NAME) > 0)
			return;

		Usuario usuario = empresa.getUsuarioAcesso();
		Contato contato = empresa.getContato();

		if (!getService().isEmailUnique(empresa.getId(), usuario.getId(), contato.getEmail())) {
			FieldError emailError = new FieldError(getModelName(), FIELD_NAME, getMessageSource()
					.getMessage("NonUnique.empresa.contato.email", new String[] { contato.getEmail() }, LOCALE));
			result.addError(emailError);
		}
	}

	/**
	 * 2?? valida????o
	 * 
	 * Verifica se o CPF informado n??o foi cadastrado anteriormente
	 * 
	 * @param empresa
	 * @param result
	 */
	private void setCNPJNotUniqueError(Empresa empresa, BindingResult result) {
		final String FIELD_NAME = "cnpj";
		if (result.getFieldErrorCount(FIELD_NAME) > 0)
			return;

		String cnpj = empresa.getUnformattedCnpj();

		if (!getService().isCNPJUnique(empresa.getId(), cnpj)) {
			FieldError cnpjError = new FieldError(getModelName(), FIELD_NAME, getMessageSource()
					.getMessage("NonUnique.empresa.cnpj", new String[] { empresa.getCnpj() }, LOCALE));
			result.addError(cnpjError);
		}
	}

	/**
	 * 2?? valida????o
	 * 
	 * Verifica se o tipo informado ?? v??lido
	 * 
	 * @param empresa
	 * @param result
	 */
	private void setTipoNotValidError(Empresa empresa, BindingResult result) {
		final String FIELD_NAME = "tipo";
		if (result.getFieldErrorCount(FIELD_NAME) > 0)
			return;

		switch (empresa.getTipo()) {
		case CLIENTE:
			if (getService().countByEmpresaId(empresa.getId(), FUNCIONARIOS) > 0)
				setTipoNotValidError(empresa.getTipo(), result, FIELD_NAME);
			break;
		case ENTREGA:
			if (getService().countByEmpresaId(empresa.getId(), PEDIDOS) > 0)
				setTipoNotValidError(empresa.getTipo(), result, FIELD_NAME);
			break;
		default:
			break;
		}
	}

	/**
	 * Verifica se ?? um tipo v??lido
	 * 
	 * @param tipo
	 * @param result
	 * @param fieldName
	 */
	private void setTipoNotValidError(Tipo tipo, BindingResult result, String fieldName) {
		FieldError tipoError = new FieldError(getModelName(), fieldName,
				getMessageSource().getMessage("Invalid.empresa.tipo", new String[] { tipo.getDescricao() }, LOCALE));
		result.addError(tipoError);
	}

	/**
	 * Carrega os entregadores da empresa de entrega
	 * 
	 * @param empresaId
	 *            ID empresa
	 * @param model
	 * @param offset
	 * @param sortField
	 * @param sortOrder
	 */
	@SuppressWarnings("unchecked")
	private void loadEntregadores(Integer empresaId, ModelMap model, Integer offset, String sortField,
			SortOrder sortOrder) {
		Map<String, Object> filters = new HashMap<>();
		filters.put("empresa.id", empresaId);
		filters.put("funcao", Funcao.ENTREGADOR);

		SearchParams params = new SearchParams(offset, MAX_RESULTS, sortField, sortOrder, filters);

		entregadorService.setIgnoredJoins(Empresa.class);
		List<Funcionario> entregadores = entregadorService.search(params);
		long count = entregadorService.getTotalElements(filters);

		model.addAttribute("entregadores", entregadores);
		model.addAttribute("escondeEmpresa", true);

		setPaginationAttribute(model, offset, sortField, sortOrder, null, count, MAX_RESULTS);
	}

	/**
	 * Carrega os pedidos da empresa cliente
	 * 
	 * @param empresaId
	 *            ID da empresa
	 * @param model
	 * @param offset
	 * @param sortField
	 * @param sortOrder
	 */
	@SuppressWarnings("unchecked")
	private void loadPedidos(Integer empresaId, ModelMap model, Integer offset, String sortField, SortOrder sortOrder) {
		Map<String, Object> filters = new HashMap<>();
		filters.put("empresa.id", empresaId);

		SearchParams params = new SearchParams(offset, MAX_RESULTS, sortField, sortOrder, filters);

		pedidoService.setIgnoredJoins(Cliente.class, Empresa.class);
		List<Pedido> pedidos = pedidoService.search(params);
		long count = pedidoService.getTotalElements(filters);

		model.addAttribute("pedidos", pedidos);
		model.addAttribute("escondeCliente", true);

		setPaginationAttribute(model, offset, sortField, sortOrder, null, count, MAX_RESULTS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractBeanController#getDeleteSucessMessage
	 * ()
	 */
	@Override
	protected String getDeleteSucessMessage() {
		return getMessageSource().getMessage("empresa.successfully.removed", null, LOCALE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractBeanController#getStoreSucessMessage(
	 * )
	 */
	@Override
	protected String getStoreSucessMessage() {
		return getMessageSource().getMessage("empresa.successfully.registered", null, LOCALE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractBeanController#getUpdateSucessMessage
	 * (br.com.cams7.app.entity.AbstractEntity)
	 */
	@Override
	protected String getUpdateSucessMessage(Empresa empresa) {
		return getMessageSource().getMessage("empresa.successfully.updated",
				new String[] { empresa.getRazaoSocialWithCnpj() }, LOCALE);
	}

}
