<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>my Tree</title>

    <sec:csrfMetaTags />

<%--
    <script src="${contextPath}/resources/fancytree/lib/jquery.js"></script>
    <script src="${contextPath}/resources/fancytree/lib/jquery-ui.custom.js"></script>
--%>


    <link href="${contextPath}/resources/css/jquery-ui.css" rel="stylesheet" >
    <script src="${contextPath}/resources/jquery/jquery-3.2.1.min.js"></script>
    <script src="${contextPath}/resources/jquery/jquery-ui.min.js"></script>

        <link href="${contextPath}/resources/fancytree/dist/skin-xp/ui.fancytree.min.css" rel="stylesheet">
    <%--    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">--%>

<%--    <link href="${contextPath}/resources/css/font-awesome.min.css" rel="stylesheet">--%>

    <%--    <link rel="stylesheet" href="${contextPath}/resources/fancytree/dist/skin-awesome/ui.fancytree.min.css">--%>
    <%--    <script src="${contextPath}/resources/fancytree/dist/jquery.fancytree-all-deps.min.js"></script>--%>
    <link href="${contextPath}/resources/fancytree/dist/skin-awesome/ui.fancytree.css" rel="stylesheet" class="skinswitcher">
    <script src="${contextPath}/resources/fancytree/src/jquery.fancytree.js"></script>
    <script src="${contextPath}/resources/fancytree/src/jquery.fancytree.glyph.js"></script>

    <script src="${contextPath}/resources/fancytree/lib/contextmenu-abs/jquery.contextMenu-custom.js"></script>
    <link href="${contextPath}/resources/fancytree/lib/contextmenu-abs/jquery.contextMenu.css" rel="stylesheet" >


    <script src="${contextPath}/resources/fancytree/src/jquery.fancytree.dnd.js"></script>
    <script src = "${contextPath}/resources/fancytree/dist/src/jquery.fancytree.edit.js"></script>


<%--    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">--%>

<script type="text/javascript">

    var csrfParameter = $("meta[name='_csrf_parameter']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var headers = {};
    headers[csrfHeader] = csrfToken;

    // --- Implement Cut/Copy/Paste --------------------------------------------
    var clipboardNode = null;
    var pasteMode = null;

    function copyPaste(action, node) {
        switch( action ) {
            case "cut":
            case "copy":
                clipboardNode = node;
                pasteMode = action;
                break;
            case "paste":
                if( !clipboardNode ) {
                    alert("Clipoard is empty.");
                    break;
                }
                if( pasteMode == "cut" ) {
                    // Cut mode: check for recursion and remove source
                    var cb = clipboardNode.toDict(true);
                    if( node.isDescendantOf(cb) ) {
                        alert("Cannot move a node to it's sub node.");
                        return;
                    }
                    node.addChildren(cb);
                    node.render();
                    clipboardNode.remove();


                    tellServerPaste(clipboardNode, node);


                } else {
                    // Copy mode: prevent duplicate keys:
                    var cb = clipboardNode.toDict(true
                        , function(dict){
                        delete dict.key; // Remove key, so a new one will be created
                    }
                    );
//                    alert("cb = " + JSON.stringify(cb));
                    node.addChildren(cb);
                    node.render();
//                    node.applyPatch(cb);

//                    clipboardNode.remove();

                    tellServerPaste(clipboardNode, node);
//                    node.applyPatch(cb);
                    node.resetLazy();
                }
               clipboardNode = pasteMode = null;

                break;
            default:
                alert("Unhandled clipboard action '" + action + "'");
        }
    }


    function tellServerPaste(clipboardNode, node) {
        var result = $.ajax({
            url: "${contextPath}/pastenode",
            dataType: "json",
            type: "POST",
            async: false,
            headers: headers,
            data: {sourceNodeId: clipboardNode.key, destinyNodeId: node.key, pasteMode: pasteMode},
            cache: false,
            success: function(msg){
            }
        });
    }


    function addNewNode(action, node) {

        var titleValue = 0;
        var keyValue = 0;

        var result = $.ajax({
            url: "${contextPath}/newnode",
            dataType: "json",
            type: "POST",
            async: false,
            headers: headers,
            data: {mode: "children", parent: node.key},
            cache: false,
            success: function(msg){
                titleValue = msg['title'];
                keyValue = msg["key"];
                //alert( "Data Saved: " + titleValue);
            }
        });

        result.done(function() {
            node.addChildren({
                key: keyValue,
                title: titleValue,
                folder: true,
                expanded: true,
                lazy: true
            });
            node.render();
//            node.applyPatch(result);
            node.resetLazy();
        });
    }


    function deleteNode(action, node) {
        var result = $.ajax({
            url: "${contextPath}/deletenode",
            dataType: "json",
            type: "POST",
            async: false,
            headers: headers,
            data: {nodeId: node.key, title: node.title},
            cache: false,
            success: function(msg){
                //alert( "Data Deleted: " + "node " + msg["key"] + ", title" + msg["title"]);
            }
        });
        result.done(function() {
            node.remove();
            node.render();
            node.applyPatch(result);
        });

    }


    function editNode(action, node) {
        node.editStart();
    }



// --- Contextmenu helper --------------------------------------------------
    function bindContextMenu(span) {
        // Add context menu to this node:
        $(span).contextMenu({menu: "myMenu"}, function(action, el, pos) {
            // The event was bound to the <span> tag, but the node object
            // is stored in the parent <li> tag
            var node = $.ui.fancytree.getNode(el);
            switch( action ) {
                case "cut":
                case "copy":
                case "paste":
                    copyPaste(action, node);
                    break;
                case "new":
                    addNewNode(action, node);
                    break;
                case "delete":
                    deleteNode(action, node);
                    break;
                case "edit":
                    editNode(action, node);
                    break;
                default:

            }
        });
    }


// Here is the tree to show
    $(function(){

        /*Load tree from Ajax JSON*/
        $("#tree2").fancytree({
            extensions: ["dnd", "edit", "glyph"],
            source: {
                url: "${contextPath}/initialise",
                dataType: "json",
                type: "POST",
                headers: headers,
                cache: false
            },
            autoExpand: false,
            lazyLoad: function(event, data){
                var node = data.node;
                data.result = $.ajax({
                    url: "${contextPath}/children",
                    dataType: "json",
                    type: "POST",
                    headers: headers,
                    data: {mode: "children", parent: node.key},
                    cache: false
                });
            },
            edit: {
//                triggerStart: ["f2", "dblclick", "shift+click", "mac+enter"],
                triggerStart: ["f2", "shift+click", "mac+enter"],
                beforeEdit: function(event, data){
                    // Return false to prevent edit mode
                },
                edit: function(event, data){
                    // Editor was opened (available as data.input)
                },
                beforeClose: function(event, data){
                    // Return false to prevent cancel/save (data.input is available)
                    console.log(event.type, event, data);
                    if( data.originalEvent.type === "mousedown" ) {
                        // We could prevent the mouse click from generating a blur event
                        // (which would then again close the editor) and return `false` to keep
                        // the editor open:
//                  data.originalEvent.preventDefault();
//                  return false;
                        // Or go on with closing the editor, but discard any changes:
//                  data.save = false;

                    }

                },
                selectMode: 3,
// glyph
                glyph: {
                    map: {
                        doc: "fa fa-file-o",
                        docOpen: "fa fa-file-o",
                        checkbox: "fa fa-square-o",
                        checkboxSelected: "fa fa-check-square-o",
                        checkboxUnknown: "fa fa-square",
                        dragHelper: "fa fa-arrow-right",
                        dropMarker: "fa fa-long-arrow-right",
                        error: "fa fa-warning",
                        expanderClosed: "fa fa-caret-right",
                        expanderLazy: "fa fa-angle-right",
                        expanderOpen: "fa fa-caret-down",
                        folder: "fa fa-folder-o",
                        folderOpen: "fa fa-folder-open-o",
                        loading: "fa fa-spinner fa-pulse"
                    }
                },
                save: function(event, data){
                    // Save data.input.val() or return false to keep editor open
                    console.log("save...", this, data);
                    // Simulate to start a slow ajax request...
                    setTimeout(function(){
                        $(data.node.span).removeClass("pending");
                        // Let's pretend the server returned a slightly modified
                        // title:
                        data.node.setTitle(data.node.title);
//Ajax edit

                                var result = $.ajax({
                                    url: "${contextPath}/editnode",
                                    dataType: "json",
                                    type: "POST",
                                    async: false,
                                    headers: headers,
                                    data: {nodeId: data.node.key, title: data.node.title},
                                    cache: false,
                                    success: function(msg){
                                        //alert( "Data Deleted: " + "node " + msg["key"] + ", title" + msg["title"]);
                                    }
                                });
                                result.done(function() {
                                    data.node.render();
                                    data.node.applyPatch(result);
                                });



                    }, 2000);
                    // We return true, so ext-edit will set the current user input
                    // as title
                    return true;
                },
                close: function(event, data){
                    // Editor was removed
                    if( data.save ) {
                        // Since we started an async request, mark the node as preliminary
                        $(data.node.span).addClass("pending");
                    }
                }
            },
            activate: function(event, data) {
                var node = data.node;
                $("#echoActivated").text(node.title + ", key=" + node.key);
            },
            click: function(event, data) {
                // Close menu on click
                if ($(".contextMenu:visible").length > 0) {
                    $(".contextMenu").hide();
//          return false;
                }
            },
            keydown: function(event, data) {
                var node = data.node;
                // Eat keyboard events, when a menu is open
                if( $(".contextMenu:visible").length > 0 )
                    return false;

                switch( event.which ) {

                    // Open context menu on [Space] key (simulate right click)
                    case 32: // [Space]
                        $(node.span).trigger("mousedown", {
                            preventDefault: true,
                            button: 2
                        })
                            .trigger("mouseup", {
                                preventDefault: true,
                                pageX: node.span.offsetLeft,
                                pageY: node.span.offsetTop,
                                button: 2
                            });
                        return false;

                    // Handle Ctrl-C, -X and -V
                    case 67:
                        if( event.ctrlKey ) { // Ctrl-C
                            copyPaste("copy", node);
                            return false;
                        }
                        break;
                    case 86:
                        if( event.ctrlKey ) { // Ctrl-V
                            copyPaste("paste", node);
                            return false;
                        }
                        break;
                    case 88:
                        if( event.ctrlKey ) { // Ctrl-X
                            copyPaste("cut", node);
                            return false;
                        }
                        break;
                }
            },
            /*Bind context menu for every node when its DOM element is created.
             We do it here, so we can also bind to lazy nodes, which do not
             exist at load-time. (abeautifulsite.net menu control does not
             support event delegation)*/
            createNode: function(event, data){
                bindContextMenu(data.node.span);
            },
            /* D'n'd, just to show it's compatible with a context menu.
             See http://code.google.com/p/dynatree/issues/detail?id=174 */
            dnd: {
                preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
                preventRecursiveMoves: true, // Prevent dropping nodes on own descendants
                autoExpandMS: 400,
                dragStart: function(node, data) {
                    return true;
                },
                dragEnter: function(node, data) {
                    //               return true;
                    if(node.parent !== data.otherNode.parent)
                        return false;
                    return ["before", "after"];
                },
                dragDrop: function(node, data) {
                    data.otherNode.moveTo(node, data.hitMode);
                }
            }
        });
    });

</script>
</head>

<body>

        <c:if test="${pageContext.request.userPrincipal.name != null}">
            <form class="container" id="logoutForm" method="POST" action="${contextPath}/logout">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>

            <h2 class="container">Welcome ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Logout</a>
            </h2>

            <br>
        </c:if>
        <br>


    <h2 class="container">Load from Ajax data:</h2><br>

    <div id="tree2" data-source="ajax" class="sampletree">
    </div>

    <!-- Definition of context menu -->
    <ul id="myMenu" class="contextMenu">
        <li class="new"><a href="#new">New</a></li>
        <li class="edit"><a href="#edit">Edit</a></li>
        <li class="cut separator"><a href="#cut">Cut</a></li>
        <li class="copy"><a href="#copy">Copy</a></li>
        <li class="paste"><a href="#paste">Paste</a></li>
        <li class="delete"><a href="#delete">Delete</a></li>
        <li class="quit separator"><a href="#quit">Quit</a></li>
    </ul>

</body>
</html>
