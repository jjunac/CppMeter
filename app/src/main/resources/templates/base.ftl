<#macro base_head subtitle="">
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
          crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="/static/style.css">
    <link rel="shortcut icon" href="/static/favicon.ico">

    <title>${subtitle} | CppMeter</title>
</#macro>
<#macro head><@base_head/></#macro>

<#macro content></#macro>

<#macro base_scripts>
    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
            crossorigin="anonymous"></script>
</#macro>
<#macro scripts><@base_scripts/></#macro>

<#macro page active_navitem>
    <!doctype html>
    <html lang="en">
    <head>
        <@head/>
    </head>
    <body>
        <nav class="navbar navbar-expand navbar-dark fixed-top bg-dark">
            <div class="d-flex flex-column flex-wrap">
                <a class="navbar-brand" href="/">
                    <img src="/static/logo.png" width="30" height="30" class="d-inline-block align-top" alt="">
                    Cpp Meter
                </a>
                <ul class="navbar-nav mb-auto mt-0 ml-auto">
                    <li class="nav-item <#if active_navitem == "overview">active</#if>">
                        <a class="nav-link py-0" href="/">Overview</a>
                    </li>
                    <#list plugins as path, name>
                        <li class="nav-item <#if active_navitem == path>active</#if>">
                            <a class="nav-link py-0" href="/${path}">${name}</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </nav>
        <div class="container-fluid">
            <@content/>
        </div>
    <@scripts/>
    </body>
    </html>
</#macro>