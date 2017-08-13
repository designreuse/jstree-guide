package system.model;

import com.fasterxml.jackson.annotation.JsonView;
import system.jsonview.Views;


public class NodeResponseBody {

    @JsonView(Views.Public.class)
    private
    String key;

    @JsonView(Views.Public.class)
    private
    String title;

    @JsonView(Views.Public.class)
    private
    String folder;

    @JsonView(Views.Public.class)
    private
    String lazy;

    @JsonView(Views.Public.class)
    private
    String expanded;

    public NodeResponseBody(String key, String title, String folder, String lazy, String expanded) {
        this.key = key;
        this.title = title;
        this.folder = folder;
        this.lazy = lazy;
        this.expanded = expanded;
    }

    public NodeResponseBody() {
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getLazy() {
        return lazy;
    }

    public void setLazy(String lazy) {
        this.lazy = lazy;
    }

    public String getExpanded() {
        return expanded;
    }

    public void setExpanded(String expanded) {
        this.expanded = expanded;
    }

}
