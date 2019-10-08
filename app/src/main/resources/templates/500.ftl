<#include "base.ftl">
<#macro head>
    <@base_head subtitle="Page not found"/>
</#macro>

<#macro content>
    <br/>
    <br/>
    <img class="img-fluid mx-auto d-block" style="width:40% !important;" src="/static/panic.gif">
    <br/>
    <br/>
    <br/>
    <h1 class="text-center">Internal server error</h1>
    <h3 class="text-center">Don't worry, everything is fine... Sorta...</h3>
</#macro>

<#macro scripts>
    <@base_scripts/>
</#macro>

<@page ""/>
