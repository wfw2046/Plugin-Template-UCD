import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.entity.mime.content.FileBody
import groovyx.net.http.*

import com.urbancode.air.AirPluginTool;
import com.urbancode.air.CommandHelper;


final def airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

// properties
final def webhook = props['webhook'];
final def slackToken = props['token'];
final def slackUsername = props['username']
final def slackChannels = props['channels'].split(",|\n")*.trim() - "";
final def fileName = props['fileName'];
final def filePath = props['filePath'];


slackChannels.each { slackChannel ->
	slackChannel = URLDecoder.decode(slackChannel, "UTF-8" );
	if (!slackChannel.startsWith("@") && !slackChannel.startsWith("#")) {
		throw new RuntimeException("ERROR:: Invalid slack channel format passed: '${slackChannel}'. Must start with either # or @.")
	}
}

slackChannels.each { slackChannel ->
	def http = new HTTPBuilder(webhook)
	File file = new File(filePath+fileName)
	
    http.request(Method.POST) { req ->
    requestContentType: "multipart/form-data"
    MultipartEntity multiPartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)
    // Adding Multi-part file parameter "imageFile"
    multiPartContent.addPart("file", new FileBody(file))
    // Adding Multi-part file parameter "imageFile"
    multiPartContent.addPart("token", new StringBody(slackToken))
    // Adding Multi-part file parameter "imageFile"
    multiPartContent.addPart("channels", new StringBody(slackChannel))
   
    req.setEntity(multiPartContent)
    response.success = { resp ,reader ->
           if (resp.statusLine.statusCode == 200) {
                     // response handling
   				println "111"
   				System.out << reader
                      }
               }
         }
}