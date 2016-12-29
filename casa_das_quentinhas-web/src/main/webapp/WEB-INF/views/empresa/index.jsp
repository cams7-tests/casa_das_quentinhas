<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<div id="top" class="row">
	<div class="col-sm-3">
		<h2>Empresas</h2>
	</div>

	<div class="col-sm-6">
		<div class="input-group h2">
			<input name="data[search]" class="form-control" id="search_query"
				type="text" placeholder="Razão social / Nome fantasia / CNPJ / E-mail"> <span
				class="input-group-btn">
				<button id="search_btn" class="btn btn-primary" type="submit">
					<span class="glyphicon glyphicon-search"></span>
				</button>
			</span>
		</div>
	</div>

	<div class="col-sm-3">
		<sec:authorize access="hasRole('GERENTE')">
			<a href="<c:url value='/empresa/create' />"
				class="btn btn-primary pull-right h2">Nova Empresa</a>
		</sec:authorize>
	</div>
</div>

<hr />

<div class="content">
	<%@include file="list.jsp"%>
</div>

<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.13/css/dataTables.bootstrap.min.css">

<script type="text/javascript">
	var MAIN_PAGE = '<c:url value='/${mainPage}' />';
	var MODAL_LABEL = 'Excluir Empresa';
	var MODAL_BODY = 'Deseja realmente excluir esta Empresa?';
</script>
<script src="<c:url value='/static/js/casa_das_quentinhas-index.js' />"></script>

<%@include file="../../layouts/delete_modal.jsp"%>