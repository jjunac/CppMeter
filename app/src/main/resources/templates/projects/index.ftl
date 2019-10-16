<#include "../base.ftl">
<#macro head>
    <@base_head subtitle="Projects"/>
</#macro>

<#macro content>
    <div class="container">
        <h1>Projects</h1>
        <#list projects as p>
            <div class="card" style="height: 150px;">
                <div class="row no-gutters">
                    <div style="width: 150px;">
                        <img src="https://via.placeholder.com/150" class="card-img" alt="...">
                    </div>
                    <div>
                        <div class="card-body">
                            <a href="/?p=${p}"><h4 class="card-title">${p}</h4></a>
                            <p class="card-text">Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
                            <p class="card-text"><small class="text-muted">Last updated 3 mins ago</small></p>
                        </div>
                    </div>
                </div>
            </div>
        </#list>
    </div>
</#macro>

<#macro scripts>
    <@base_scripts/>
</#macro>

<@page/>
