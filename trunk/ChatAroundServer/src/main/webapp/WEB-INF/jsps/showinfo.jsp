<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
 
</head>
<body>
<table>
<tr>
<td>Grid Size(In Degree)</td>
<td>:</td>
<td><c:out value="${BoxSizeInDegree}"></c:out></td>
</tr>
<tr>
<td>Grid Size(In Km)</td>
<td>:</td>
<td><c:out value="${BoxSizeInKm}"></c:out></td>
</tr>
<tr>
<td>Total Users</td>
<td>:</td>
<td><c:out value="${TotalUsers}"></c:out></td>
</tr>
<tr>
<td>Total Locations</td>
<td>:</td>
<td><c:out value="${TotalLocations}"></c:out></td>
</tr>
</table>
<c:out value="${Error}"></c:out>

<table>
<c:forEach items="${counterMap}" var="counterMap" varStatus="status">
        <tr>
            <td>${counterMap.key}</td>
            <td><c:out value="${counterMap.value}"/></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
