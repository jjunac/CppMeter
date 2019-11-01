<#include "base.ftl">
<#macro head>
    <@base_head subtitle="Analysis"/>
</#macro>

<#macro content>
    <div class="row h-100 justify-content-center align-items-center text-center">
        <div class="spinner-border text-success" role="status">
            <span class="sr-only">Loading...</span>
        </div>
        <h2 id="analysis-status">Starting analysis...</h2>
    </div>
</#macro>

<#macro scripts>
    <@base_scripts/>
    <script>
        setInterval(function () {
            $.ajax({
                type: "GET",
                dataType: "test",
                url: "/analysis/status",
                data: "p=${activeProject}",
                complete: function(data) {
                    console.log(data);
                    var json = $.parseJSON(data.responseText);
                    $('#analysis-status').text(json["description"]);
                    if (!json["isOngoing"]) {
                        location.reload();
                    }
                }
            })
        }, 500);
    </script>
</#macro>

<@page/>
