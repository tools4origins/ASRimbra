package edu.tsp.asr.transformers;

import com.thoughtworks.xstream.XStream;
import spark.ResponseTransformer;

public class XMLTransformer implements ResponseTransformer {
    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private XStream xStream = new XStream();

    @Override
    public String render(Object model) {
        return HEADER + xStream.toXML(model);
    }

}