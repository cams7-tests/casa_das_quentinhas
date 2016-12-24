<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<div id="top" class="row">
	<div class="col-sm-3">
		<h2>Usuários</h2>
	</div>

	<div class="col-sm-6">
		<div class="input-group h2">
			<input name="data[search]" class="form-control" id="search_query" type="text" placeholder="Pesquisar Usuários"> 
			<span class="input-group-btn">
				<button id="search_btn" class="btn btn-primary" type="submit">
					<span class="glyphicon glyphicon-search"></span>
				</button>
			</span>
		</div>
	</div>

	<div class="col-sm-3">
		<sec:authorize access="hasRole('ADMIN')">
			<a href="<c:url value='/usuario/create' />"
				class="btn btn-primary pull-right h2">Novo Usuário</a>
		</sec:authorize>
	</div>
</div>
<!-- /#top -->

<hr />

<div class="content">
	<%@include file="list.jsp"%>
</div>

<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.13/css/dataTables.bootstrap.min.css">

<script type="text/javascript">
	var MAIN_PAGE = '<c:url value='/${mainPage}' />';
</script>	
<script src="<c:url value='/static/js/casa_das_marmitas-list.js' />"></script>


