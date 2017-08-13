package system.controller;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import system.dto.NodeDto;
import system.jsonview.Views;
import system.model.NodeResponseBody;
import system.service.NodeService;
import system.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class TreeController {

    private static final Logger logger = LoggerFactory.getLogger(TreeController.class);


    @Autowired
    private UserService userService;

    @Autowired
    private NodeService nodeService;


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/initialise", method = RequestMethod.POST)
    public @ResponseBody
    List<NodeResponseBody> initialiseViaAjax() {
        List<NodeResponseBody> result;
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        result = nodeService.findChildren(userService.findUserByLogin(username).getUserId(), 0);
        return result;
    }


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/children", method = RequestMethod.POST)
    public @ResponseBody
    List<NodeResponseBody> getChildren(String mode, String parent) {

        logger.info("RequestBody from ajax lazyloading: mode - " + mode + " , parent - " + parent);

        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        List<NodeResponseBody> result;
        result = nodeService.findChildren(userService.findUserByLogin(username).getUserId(), Integer.parseInt(parent));
        return result;
    }


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/newnode", method = RequestMethod.POST)
    public @ResponseBody
    NodeResponseBody newNode(String mode, String parent) {

        logger.info("RequestBody from ajax newNode(): mode = " + mode + ", parentId = " + parent);

        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        NodeDto nodeDto = nodeService.save(parent, username);
        NodeResponseBody response = new NodeResponseBody(
                String.valueOf(nodeDto.getNodeId()), nodeDto.getContent(), "true", "true", "false");
        return response;
    }


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/deletenode", method = RequestMethod.POST)
    public @ResponseBody
    NodeResponseBody deleteNode(String nodeId, String title) {

        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        logger.info("RequestBody from ajax deleteNode(): title = " + title + ", parentId = " + nodeId);

        nodeService.delete(nodeId, username);

        NodeResponseBody response = new NodeResponseBody(nodeId, title, "true", "true", "false");
        return response;
    }


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/editnode", method = RequestMethod.POST)
    public @ResponseBody
    NodeResponseBody editNode(String nodeId, String title) {

        logger.info("RequestBody from ajax editNode(): title = " + title + ", parentId = " + nodeId);

        nodeService.edit(nodeId, title);
        NodeResponseBody response = new NodeResponseBody(nodeId, title, "true", "true", "false");
        return response;
    }


    @JsonView(Views.Public.class)
    @RequestMapping(value = "/pastenode", method = RequestMethod.POST)
    public @ResponseBody
    NodeResponseBody pasteNode(String sourceNodeId, String destinyNodeId, String pasteMode) {

        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        
        logger.info("RequestBody from ajax pasteNode(): sourceID = "
                + sourceNodeId + ", destID = " + destinyNodeId + "; pasteMode = " + pasteMode);

        nodeService.paste(sourceNodeId, destinyNodeId, username, pasteMode);
        NodeResponseBody response = new NodeResponseBody(sourceNodeId, destinyNodeId, "true", "true", "false");
        return response;
    }


}
