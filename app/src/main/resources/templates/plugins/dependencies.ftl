<#include "../base.ftl">
<#macro head>
    <@base_head subtitle="Dependencies"/>
    <link rel="stylesheet" href="https://unpkg.com/tippy.js@4.0.1/index.css" />
</#macro>

<#macro content>
    <h1>Deps</h1>
    <div id="cy" style="width: 100%; height: 100%; position: absolute; top: 0px; left: 0px;"></div>
</#macro>

<#macro scripts>
    <@base_scripts/>
    <script src="https://unpkg.com/tippy.js@4.0.1/umd/index.all.min.js"></script>
    <script src="https://unpkg.com/cytoscape/dist/cytoscape.min.js"></script>
    <script src="/static/libs/cytoscape-popper.min.js"></script>
    <script>
        console.log('Constructing deps graph...');
        var cy = cytoscape({
            container: document.getElementById('cy'), // container to render in
            elements: [ // list of graph elements to start with
                <#list internalFiles as f>
                    { data: { id: "${f}", type: "internal" } },
                </#list>
                <#list externalFiles as f>
                    { data: { id: "${f}", type: "external" } },
                </#list>
                <#list deps as source, targets>
                    <#list targets as target>
                        { data: { id: "${source}${target}", source: "${source}", target: "${target}" } },
                    </#list>
                </#list>
            ],
            style: [ // the stylesheet for the graph
                {
                    selector: '[type = "internal"]',
                    style: {
                        'background-color': '#8BC34A',
                    }
                }, {
                    selector: '[type = "external"]',
                    style: {
                        'background-color': '#03A9F4',
                    }
                }, {
                    selector: 'edge',
                    style: {
                        'width': 3,
                        'line-color': '#ccc',
                        'target-arrow-color': '#ccc',
                        'target-arrow-shape': 'triangle'
                    }
                }
            ],
                layout: {
                name: 'circle',
            }
        });
        cy.nodes().forEach(n => {
            n.tippy = tippy(n.popperRef(), {
                content: () => {    // tippy options:
                    let div = document.createElement('div');
                    div.classList.add('popper-div');
                    div.innerHTML = n.id();
                    document.body.appendChild(div);
                    return div;
                },
                trigger: 'manual',  // probably want manual mode
                    hideOnClick: false
            })
        });
        cy.on('mouseover', 'node', e => e.target.tippy.show());
        cy.on('mouseout', 'node', e => e.target.tippy.hide());
        console.log('Done');
    </script>
</#macro>

<@page "dependencies"/>
