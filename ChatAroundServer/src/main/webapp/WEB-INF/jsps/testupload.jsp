<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Upload a file please</title>
</head>
<body>
  <h1>Please upload a file</h1>
  <form method="POST" action="/ChatAroundServer/api/1.0/uploadimage" enctype="multipart/form-data">
      <input type="text" name="UserId"/>
      <input type="file" name="file"/>
      <input type="submit"/>
  </form>
</body>
</html>