<#include "base.ftl">
<#macro head>
    <@base_head subtitle="Page not found"/>
</#macro>

<#macro content>
    <br/>
    <br/>
    <img class="img-fluid w-50 mx-auto d-block" src="/static/travolta.gif">
    <br/>
    <br/>
    <br/>
    <h1 class="text-center">Page not found</h1>
    <h3 class="text-center">Oops! It seems like you are lost...</h3>
</#macro>

<#macro scripts>
    <@base_scripts/>
</#macro>

<@page ""/>
