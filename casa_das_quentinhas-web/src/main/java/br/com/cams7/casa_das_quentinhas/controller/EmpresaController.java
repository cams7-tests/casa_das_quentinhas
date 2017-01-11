/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.controller;

import static br.com.cams7.casa_das_quentinhas.model.Empresa.RelacionamentoEmpresa.FUNCIONARIOS;
import static br.com.cams7.casa_das_quentinhas.model.Empresa.RelacionamentoEmpresa.PEDIDOS;
import static br.com.cams7.casa_das_quentinhas.model.Empresa.Tipo.CLIENTE;
import static br.com.cams7.casa_das_quentinhas.model.Empresa.Tipo.ENTREGA;
import static org.springframework.http.HttpStatus.OK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import br.com.cams7.app.controller.AbstractController;
import br.com.cams7.app.utils.SearchParams;
import br.com.cams7.app.utils.SearchParams.SortOrder;
import br.com.cams7.casa_das_quentinhas.model.Cidade;
import br.com.cams7.casa_das_quentinhas.model.Cliente;
import br.com.cams7.casa_das_quentinhas.model.Contato;
import br.com.cams7.casa_das_quentinhas.model.Empresa;
import br.com.cams7.casa_das_quentinhas.model.Empresa.RegimeTributario;
import br.com.cams7.casa_das_quentinhas.model.Empresa.Tipo;
import br.com.cams7.casa_das_quentinhas.model.Endereco;
import br.com.cams7.casa_das_quentinhas.model.Funcionario;
import br.com.cams7.casa_das_quentinhas.model.Funcionario.Funcao;
import br.com.cams7.casa_das_quentinhas.model.Pedido;
import br.com.cams7.casa_das_quentinhas.model.Usuario;
import br.com.cams7.casa_das_quentinhas.service.CidadeService;
import br.com.cams7.casa_das_quentinhas.service.EmpresaService;
import br.com.cams7.casa_das_quentinhas.service.FuncionarioService;
import br.com.cams7.casa_das_quentinhas.service.PedidoService;

/**
 * @author César Magalhães
 *
 */
@Controller
@RequestMapping("/" + EmpresaController.MODEL_NAME)
@SessionAttributes({ "empresaTipos", "empresaRegimesTributarios" })
public class EmpresaController extends AbstractController<EmpresaService, Empresa, Integer> {

	public static final String MODEL_NAME = "empresa";
	public static final String LIST_NAME = "empresas";

	@Autowired
	private CidadeService cidadeService;

	@Autowired
	private FuncionarioService entregadorService;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private MessageSource messageSource;

	private final short MAX_RESULTS = 5;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractController#store(br.com.cams7.app.
	 * model.AbstractEntity, org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, java.lang.Integer)
	 */
	@Override
	public String store(@Valid Empresa empresa, BindingResult result, ModelMap model,
			@RequestParam(value = LAST_LOADED_PAGE, required = true) Integer lastLoadedPage) {

		setCommonAttributes(model);
		incrementLastLoadedPage(model, lastLoadedPage);

		Usuario usuario = empresa.getUsuarioAcesso();
		Cidade cidade = empresa.getCidade();

		// 1º validação
		if (cidade.getId() == null) {
			FieldError cidadeError = new FieldError(getModelName(), "cidade.id",
					messageSource.getMessage("NotNull.empresa.cidade.id", null, LOCALE));
			result.addError(cidadeError);
		}

		if (usuario.getSenha().isEmpty()) {
			FieldError senhaError = new FieldError(getModelName(), "usuarioAcesso.senha",
					messageSource.getMessage("NotEmpty.empresa.usuarioAcesso.senha", null, LOCALE));
			result.addError(senhaError);
		}

		setNotEmptyConfirmacaoError(usuario, result, true);

		// 2º validação
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

		return redirectMainPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractController#show(java.io.Serializable,
	 * org.springframework.ui.ModelMap)
	 */
	@Override
	public String show(@PathVariable Integer id, ModelMap model) {

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

		return super.show(id, model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractController#update(br.com.cams7.app.
	 * model.AbstractEntity, org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, java.io.Serializable, java.lang.Integer)
	 */
	@Override
	public String update(@Valid Empresa empresa, BindingResult result, ModelMap model, @PathVariable Integer id,
			@RequestParam(value = LAST_LOADED_PAGE, required = true) Integer lastLoadedPage) {

		setCommonAttributes(model);
		incrementLastLoadedPage(model, lastLoadedPage);
		setEditPage(model);

		Usuario usuario = empresa.getUsuarioAcesso();

		// 1º validação
		setNotEmptyConfirmacaoError(usuario, result, !usuario.getSenha().isEmpty());

		// 2º validação
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

		return redirectMainPage();
	}

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

	@GetMapping(value = "/cidades/{nomeOrIbge}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Integer, String>> getCidades(@PathVariable String nomeOrIbge) {
		Map<Integer, String> cidades = cidadeService.getCidadesByNomeOrIbge(nomeOrIbge);

		return new ResponseEntity<Map<Integer, String>>(cidades, OK);
	}

	/**
	 * Possiveis tipos de empresa
	 */
	@ModelAttribute("empresaTipos")
	public Tipo[] initializeTipos() {
		return Tipo.values();
	}

	/**
	 * Possiveis regimes tributários
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

	@Override
	protected String getDeleteMessage() {
		return "A empresa foi removida com sucesso.";
	}

	/**
	 * 1º validação
	 * 
	 * Verifica se o campo de confirmação de senha não esta vazio, caso o campo
	 * senha tenha sido preenchido anteriormente
	 * 
	 * @param usuario
	 *            Usuário
	 * @param result
	 * @param senhaInformada
	 *            A senha foi informada
	 */
	private void setNotEmptyConfirmacaoError(Usuario usuario, BindingResult result, boolean senhaInformada) {
		if (senhaInformada && usuario.getConfirmacaoSenha().isEmpty()) {
			FieldError confirmacaoError = new FieldError(getModelName(), "usuarioAcesso.confirmacaoSenha",
					messageSource.getMessage("NotEmpty.usuario.confirmacaoSenha", null, LOCALE));
			result.addError(confirmacaoError);
		}
	}

	/**
	 * 2º validação
	 * 
	 * Verifica se senha informada é mesma do campo confirmação
	 * 
	 * @param usuario
	 *            Usuário
	 * @param result
	 */
	private void setSenhaNotEqualsConfirmacaoError(Usuario usuario, BindingResult result) {
		final String FIELD_NAME = "usuarioAcesso.confirmacaoSenha";
		if (result.getFieldErrorCount(FIELD_NAME) > 0)
			return;

		if (!usuario.getSenha().isEmpty() && !usuario.getConfirmacaoSenha().isEmpty()
				&& !usuario.getSenha().equals(usuario.getConfirmacaoSenha())) {
			FieldError confirmacaoError = new FieldError(getModelName(), FIELD_NAME,
					messageSource.getMessage("NotEquals.usuario.confirmacaoSenha", null, LOCALE));
			result.addError(confirmacaoError);
		}
	}

	/**
	 * 2º validação
	 * 
	 * Verifica se o e-mail informado não foi cadastrado anteriormente
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
			FieldError emailError = new FieldError(getModelName(), FIELD_NAME, messageSource
					.getMessage("NonUnique.empresa.contato.email", new String[] { contato.getEmail() }, LOCALE));
			result.addError(emailError);
		}
	}

	/**
	 * 2º validação
	 * 
	 * Verifica se o CPF informado não foi cadastrado anteriormente
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
			FieldError cnpjError = new FieldError(getModelName(), FIELD_NAME,
					messageSource.getMessage("NonUnique.empresa.cnpj", new String[] { empresa.getCnpj() }, LOCALE));
			result.addError(cnpjError);
		}
	}

	/**
	 * 2º validação
	 * 
	 * Verifica se o tipo informado é valido
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

	private void setTipoNotValidError(Tipo tipo, BindingResult result, String fieldName) {
		FieldError tipoError = new FieldError(getModelName(), fieldName,
				messageSource.getMessage("Invalid.empresa.tipo", new String[] { tipo.getDescricao() }, LOCALE));
		result.addError(tipoError);
	}

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

}
