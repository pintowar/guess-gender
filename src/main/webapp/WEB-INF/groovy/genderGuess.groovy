import static gender.guess.Guessing.*
import javax.servlet.http.HttpServletResponse
import groovy.json.JsonBuilder
import groovy.xml.MarkupBuilder

String format(HttpServletResponse response, String format, Map resp){
  def jsonBuilder = new groovy.json.JsonBuilder()

  switch(format) {
    case 'xml':
      response.setHeader "Content-Type", "application/xml"
      def writer = new StringWriter()
      def xmlBuilder = new MarkupBuilder(writer)
      xmlBuilder.root renderMap(resp)
      writer.toString()
    break
    case 'json':
      response.setHeader "Content-Type", "application/json"
      jsonBuilder.call(resp)
      jsonBuilder.toString()
    break
    default:
      response.setHeader "Content-Type", "application/json"
      response.status = 400
      jsonBuilder.call([errors:[[message:'No format found.']]])
      jsonBuilder.toString()
  }

}

if(params.name){
  out.println format(response, params.format, [name: params.name, gender: guess(params.name)])
} else{
  response.status = 400
  out.println format(response, (params.format ?: 'json'), [errors:[[message:'No name provided.']]])
}