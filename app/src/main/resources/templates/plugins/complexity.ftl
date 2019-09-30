<#include "../base.ftl">
<#macro head>
    <@base_head subtitle="Complexity"/>
</#macro>

<#macro content>
    <div id="function-complexity-sunburst" style="width: 100%; height: 100%;"></div>
</#macro>

<#macro scripts>
    <@base_scripts/>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
    <script>
        var data = [{
            type: "sunburst",
            ids: [<#list ids as id>"${id}",</#list>],
            labels: [<#list labels as label>"${label}",</#list>],
            parents: [<#list parents as parent>"${parent}",</#list>],
            values: [<#list values as value>${value?c},</#list>],
            branchvalues: "total",
            outsidetextfont: {size: 20, color: "#377eb8"},
            marker: {
                line: {width: 2},
                colors: [<#list colors as color>"${color}",</#list>]
            },
            leaf: {opacity: 1}
        }];

        var layout = {
            margin: {l: 0, r: 0, b: 0, t:0},
            autosize: true,
        };
        Plotly.newPlot('function-complexity-sunburst', data, layout, {responsive: true});
    </script>
</#macro>

<@page "complexity"/>
