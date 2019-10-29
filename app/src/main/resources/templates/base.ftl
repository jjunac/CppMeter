<#macro base_head subtitle="">
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
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
    <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
            crossorigin="anonymous"></script>
</#macro>
<#macro scripts><@base_scripts/></#macro>

<#macro page activeNavitem="">
    <!doctype html>
    <html lang="en">
    <head>
        <@head/>
    </head>
    <body>
        <nav class="navbar navbar-expand navbar-dark fixed-top bg-dark">
            <div class="d-flex flex-column w-100">
                <div class="d-flex navbar-nav">
                    <a class="navbar-brand mr-2" href="/">
                        <img src="/static/logo.png" width="30" height="30" class="d-inline-block align-top" alt="">
                        Cpp Meter
                    </a>
                    <span class="pipe-separator py-1">|</span>
                    <div class="mr-auto dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <#if activeProject?has_content>
                                ${activeProject}
                            <#else>
                                All projects
                            </#if>
                        </a>
                        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <a class="dropdown-item" href="/projects">All projects</a>
                            <div class="dropdown-divider"></div>
                            <#list projects as p>
                                <a class="dropdown-item" href="/?p=${p}">${p}</a>
                            </#list>
                        </div>
                    </div>
                    <a class="btn btn-success mx-3" href="/projects/new">Add project</a>
                    <div class="d-flex flex-column text-right version">
                        <span class="navbar-text py-0" style="letter-spacing: -0.011em;">Cpp Meter version ${version}</span>
                        <span class="navbar-text py-0">CODENAME: ${codename}</span>
                    </div>
                </div>
                <#if activeProject?has_content>
                    <ul class="navbar-nav">
                        <li class="nav-item <#if activeNavitem == "overview">active</#if>">
                            <a class="nav-link py-0" href="/?p=${activeProject}">Overview</a>
                        </li>
                        <#list plugins as path, name>
                            <li class="nav-item <#if activeNavitem == path>active</#if>">
                                <a class="nav-link py-0" href="/${path}?p=${activeProject}">${name}</a>
                            </li>
                        </#list>
                    </ul>
                </#if>
            </div>
        </nav>
        <div class="container-fluid h-100">
            <@content/>
        </div>
    <@scripts/>
    </body>
    </html>
</#macro>
