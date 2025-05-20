from rdflib import Graph, Namespace
from jinja2 import Template

# Load RDF file
g = Graph()
g.parse("ai4eosc-category-tasktype-2025-05-20.ttl", format="ttl")  # or "turtle", "n3", etc.

# Define the relevant namespace (replace with the actual one if known)
IT6 = Namespace("http://data.europa.eu/it6/")
DCT = Namespace("http://purl.org/dc/terms/")
RDF = Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")

# Extract data: Get all libraries and their names (adjust properties accordingly)
libraries = []
for s in g.subjects(predicate=RDF.type, object=IT6.Library):
    title = g.value(subject=s, predicate=DCT.title)
    version = g.value(subject=s, predicate=IT6.version)
    libraries.append({
        "uri": str(s) if s else "",
        "title": str(title) if title else "",
        "version": str(version) if version else ""
    })

# HTML Template
html_template = """
<!DOCTYPE html>
<html>
    <head>
        <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Libraries</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script><script src="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/js/bootstrap.min.js"></script>
        <style>
            .kwic-left {
                float:left;
                width:35%;
                margin-right: 0.8em;
                direction: rtl;
                overflow: visible;
                white-space:nowrap;
            }
            .kwic-row ul {
                list-style-type:none;
                margin-left:0.8em;
                display:inline;
            }
            .kwic-row ul li {
                display:inline;
            }
            .kwac-row .att {
                margin-bottom:0px;
            }
            .kwac-row .alt {
                font-style:italic;
                text-decoration:none;
            }
            .kwic-row .alt {
                font-style:italic;
                text-decoration:none;
            }
            .att {
                margin-left: 1em;
                list-style: none;
            }
            .att > li { font-size: 80% }
            .ext-link {
                background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAVklEQVR4Xn3PgQkAMQhDUXfqTu7kTtkpd5RA8AInfArtQ2iRXFWT2QedAfttj2FsPIOE1eCOlEuoWWjgzYaB/IkeGOrxXhqB+uA9Bfcm0lAZuh+YIeAD+cAqSz4kCMUAAAAASUVORK5CYII=');
                background-repeat: no-repeat;
                background-position : 100% 50%;
                padding-right:13px;
                cursor:pointer;
            } 
            .pref {
                font-weight: bold;
            }
            .alt {
                font-style:italic;
                text-decoration:line-through;
            }
            .alt-att {
                font-style:italic;
            }
        </style>
    </head>
    <body style="margin-bottom: 40px;">
        <div class="container">
            <div class="header">
                <h1></h1>
                <div></div>
            </div>
            <div class="display">
                <div id="Libraries" class="section">
                    <h2 class="title">Libraries</h2>
                    <ul>
                    {% for lib in libraries %}
                        <li><span class="ext-uri"><a class="pref" href="{{ lib.uri}}">{{ lib.title}}</a></span><br>{{ lib.version }}</li>
                    {% endfor %}
                    </ul>
                </div>
            </div>
        </div>
        <script>
            $(document).ready(function () {
            // add external link behavior to every external link
            /*
            $('span[title]:not(:has(a))').mouseover(function() {
                $(this).addClass('ext-link');
            });
            $('span[title]:not(:has(a))').mouseout(function() {
                $(this).removeClass('ext-link');
            });
            $('span[title]:not(:has(a))').click(function() {
                window.open($(this).attr('title'));
                // change this to the following line to have links open in same window/tab
                // document.location.href = $(this).attr('title');
            });
            */

            // gestion des liens externes
            $('.ext-uri').mouseover(function() {
                $(this).addClass('ext-link');
            });
            $('.ext-uri').mouseout(function() {
                $(this).removeClass('ext-link');
            });
            $('.ext-uri').click(function() {
                window.open($(this).attr('title'));
                // change this to the following line to have links open in same window/tab
                // document.location.href = $(this).attr('title');
            });

            });
        </script>
    </body>
</html>
"""

# Render HTML
template = Template(html_template)
html_output = template.render(libraries=libraries)

# Save to file
with open("libraries.html", "w", encoding="utf-8") as f:
    f.write(html_output)

print("HTML page generated: libraries.html")