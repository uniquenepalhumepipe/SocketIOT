package app.socketiot.server.api.model;

import java.util.List;
import app.socketiot.server.core.json.model.Widget;

public class WidgetReq {
    public String blueprint_id;
    public List<Widget> widgets;

    public WidgetReq() {
    }
}