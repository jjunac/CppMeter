<#include "../base.ftl">
<#macro head>
    <@base_head subtitle="Projects"/>
</#macro>

<#macro content>
    <div class="container">
        <h1>Add a new project</h1>
        <form action="/projects" method="post">
            <div class="form-group row">
                <label for="inputName" class="col-sm-4 col-form-label">Name</label>
                <div class="col-sm-8">
                    <input type="text" class="form-control" id="inputName" placeholder="My Project" name="name">
                </div>
            </div>
            <div class="form-group row">
                <label for="inputPath" class="col-sm-4 col-form-label">Path</label>
                <div class="col-sm-8">
                    <input type="text" class="form-control" id="inputPath" placeholder="/path/to/my/project" name="path">
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-8">
                    <input type="submit" class="btn btn-success" value="Add">
                </div>
            </div>
        </form>
    </div>
</#macro>

<#macro scripts>
    <@base_scripts/>
</#macro>

<@page/>
