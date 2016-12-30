/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.controller;

import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import br.com.cams7.app.controller.AbstractController;
import br.com.cams7.casa_das_quentinhas.model.Cidade;
import br.com.cams7.casa_das_quentinhas.model.Cliente;
import br.com.cams7.casa_das_quentinhas.model.Cliente.TipoContribuinte;
import br.com.cams7.casa_das_quentinhas.model.Contato;
import br.com.cams7.casa_das_quentinhas.model.Endereco;
import br.com.cams7.casa_das_quentinhas.model.Usuario;
import br.com.cams7.casa_das_quentinhas.service.CidadeService;
import br.com.cams7.casa_das_quentinhas.service.ClienteService;
import br.com.cams7.casa_das_quentinhas.service.UsuarioService;

/**
 * @author César Magalhães
 *
 */
@Controller
@RequestMapping("/cliente")
@SessionAttributes("clienteTiposContribuintes")
public class ClienteController extends AbstractController<ClienteService, Cliente, Integer> {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CidadeService cidadeService;

	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.controller.AbstractController#store(br.com.cams7.app.
	 * model.AbstractEntity, org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, java.lang.Integer)
	 */
	@Override
	public String store(@Valid Cliente cliente, BindingResult result, ModelMap model,
			@RequestParam(value = LAST_LOADED_PAGE, required = true) Integer lastLoadedPage) {

		Usuario usuario = cliente.getUsuarioAcesso();
		Cidade cidade = cliente.getCidade();

		if (cidade.getId() == null) {
			FieldError cidadeError = new FieldError("cliente", "cidade.id",
					messageSource.getMessage("NotNull.cliente.cidade.id", null, Locale.getDefault()));
			result.addError(cidadeError);
		}

		if (usuario.getSenha().isEmpty()) {
			FieldError senhaError = new FieldError("cliente", "usuarioAcesso.senha",
					messageSource.getMessage("NotEmpty.cliente.usuarioAcesso.senha", null, Locale.getDefault()));
			result.addError(senhaError);
		}

		setNotEmptyConfirmacaoError(usuario, result, true);
		setSenhaNotEqualsConfirmacaoError(usuario, result);

		setEmailNotUniqueError(cliente, result);
		setCPFNotUniqueError(cliente, result);

		setUsuarioLogado(model);
		incrementLastLoadedPage(model, lastLoadedPage);
		setMainPage(model);

		if (result.hasErrors())
			return getCreateTilesPage();

		usuario = new Usuario(usuarioService.getUsuarioIdByEmail(getUsername()));
		cliente.setUsuarioCadastro(usuario);

		cliente.setCpf(cliente.getUnformattedCpf());
		cliente.getContato().setTelefone(cliente.getContato().getUnformattedTelefone());
		cliente.getEndereco().setCep(cliente.getEndereco().getUnformattedCep());

		getService().persist(cliente);

		return redirectMainPage();
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
	public String update(@Valid Cliente cliente, BindingResult result, ModelMap model, @PathVariable Integer id,
			@RequestParam(value = LAST_LOADED_PAGE, required = true) Integer lastLoadedPage) {
		Usuario usuario = cliente.getUsuarioAcesso();

		setNotEmptyConfirmacaoError(usuario, result, !usuario.getSenha().isEmpty());
		setSenhaNotEqualsConfirmacaoError(usuario, result);

		setEmailNotUniqueError(cliente, result);
		setCPFNotUniqueError(cliente, result);

		setEditPage(model);
		setUsuarioLogado(model);
		incrementLastLoadedPage(model, lastLoadedPage);
		setMainPage(model);

		if (result.hasErrors())
			return getEditTilesPage();

		cliente.setCpf(cliente.getUnformattedCpf());
		cliente.getContato().setTelefone(cliente.getContato().getUnformattedTelefone());
		cliente.getEndereco().setCep(cliente.getEndereco().getUnformattedCep());

		getService().update(cliente);

		return redirectMainPage();
	}

	@GetMapping(value = { "/cidades/{nomeOrIbge}" }, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Integer, String>> getCidades(@PathVariable String nomeOrIbge) {
		Map<Integer, String> cidades = cidadeService.getCidadesByNomeOrIbge(nomeOrIbge);

		return new ResponseEntity<Map<Integer, String>>(cidades, HttpStatus.OK);
	}

	/**
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
			FieldError confirmacaoError = new FieldError("cliente", "usuarioAcesso.confirmacaoSenha",
					messageSource.getMessage("NotEmpty.usuario.confirmacaoSenha", null, Locale.getDefault()));
			result.addError(confirmacaoError);
		}
	}

	/**
	 * Verifica se senha informada é mesma do campo confirmação
	 * 
	 * @param usuario
	 *            Usuário
	 * @param result
	 */
	private void setSenhaNotEqualsConfirmacaoError(Usuario usuario, BindingResult result) {
		if (result.getFieldErrorCount("usuarioAcesso.confirmacaoSenha") > 0)
			return;

		if (!usuario.getSenha().isEmpty() && !usuario.getConfirmacaoSenha().isEmpty()
				&& !usuario.getSenha().equals(usuario.getConfirmacaoSenha())) {
			FieldError confirmacaoError = new FieldError("cliente", "usuarioAcesso.confirmacaoSenha",
					messageSource.getMessage("NotEquals.usuario.confirmacaoSenha", null, Locale.getDefault()));
			result.addError(confirmacaoError);
		}
	}

	/**
	 * Verifica se o e-mail informado não foi cadastrado anteriormente
	 * 
	 * @param cliente
	 * @param result
	 */
	private void setEmailNotUniqueError(Cliente cliente, BindingResult result) {
		if (result.getFieldErrorCount("contato.email") > 0)
			return;

		Usuario usuario = cliente.getUsuarioAcesso();
		Contato contato = cliente.getContato();

		if (!getService().isEmailUnique(cliente.getId(), usuario.getId(), contato.getEmail())) {
			FieldError emailError = new FieldError("cliente", "contato.email", messageSource.getMessage(
					"NonUnique.cliente.contato.email", new String[] { contato.getEmail() }, Locale.getDefault()));
			result.addError(emailError);
		}
	}

	/**
	 * Verifica se o CPF informado não foi cadastrado anteriormente
	 * 
	 * @param cliente
	 * @param result
	 */
	private void setCPFNotUniqueError(Cliente cliente, BindingResult result) {
		if (result.getFieldErrorCount("cpf") > 0)
			return;

		String cpf = cliente.getUnformattedCpf();

		if (!getService().isCPFUnique(cliente.getId(), cpf)) {
			FieldError cpfError = new FieldError("cliente", "cpf", messageSource.getMessage("NonUnique.cliente.cpf",
					new String[] { cliente.getCpf() }, Locale.getDefault()));
			result.addError(cpfError);
		}
	}

	/**
	 * Possiveis tipos de contribuintes
	 */
	@ModelAttribute("clienteTiposContribuintes")
	public TipoContribuinte[] initializeTipos() {
		TipoContribuinte[] tipos = TipoContribuinte.values();
		return tipos;
	}

	@Override
	protected String getEntityName() {
		return "cliente";
	}

	@Override
	protected String getListName() {
		return "clientes";
	}

	@Override
	protected String getMainPage() {
		return "cliente";
	}

	@Override
	protected String getIndexTilesPage() {
		return "cliente_index";
	}

	@Override
	protected String getCreateTilesPage() {
		return "cliente_create";
	}

	@Override
	protected String getShowTilesPage() {
		return "cliente_show";
	}

	@Override
	protected String getEditTilesPage() {
		return "cliente_edit";
	}

	@Override
	protected String getListTilesPage() {
		return "cliente_list";
	}

	@Override
	protected String[] getGlobalFilters() {
		return new String[] { "nome", "cpf", "contato.email", "contato.telefone", "cidade.nome" };
	}

	@Override
	protected Cliente getNewEntity() {
		Cliente cliente = new Cliente();
		cliente.setCidade(new Cidade());
		cliente.setUsuarioAcesso(new Usuario());
		cliente.setEndereco(new Endereco());
		cliente.setContato(new Contato());

		return cliente;
	}

	@Override
	protected Cliente getEntity(Integer id) {
		Cliente cliente = getService().getClienteById(id);
		return cliente;
	}

}