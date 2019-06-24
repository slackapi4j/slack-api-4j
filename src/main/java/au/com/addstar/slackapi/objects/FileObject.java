package au.com.addstar.slackapi.objects;

/**
 * Created by benjamincharlton on 26/08/2018.
 */
public class FileObject extends TimeStampedBaseObject {
    private String name;
    private String title;
    private String mimeType;
    private String fileType;
    private String prettyType;
    private ObjectID user;
    private boolean editable;
    private long size;
    private Mode mode;
    private boolean isExternal;
    private boolean isPublic;
    private boolean publicUrlShared;
    private boolean displayAsBot;

    enum Mode {

        HOSTED("hosted"),
        EXTERNAL("external"),
        SNIPPET("snippet"),
        POST("post");

        private String mode;
        Mode(String mode){
            this.mode = mode;
        }
        public String getMode(){
            return mode;
        }
    }
}
