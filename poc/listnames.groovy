/*
 * Script to capture the initial database from
 * http://www.dicionariodenomesproprios.com.br
 */
@Grab(group='org.jsoup', module='jsoup', version='1.7.2') 
import org.jsoup.Jsoup

def listNames(url, element, cback){
  def doc = Jsoup.connect(url).get()
  def elements = doc.select element
  cback(elements.text().split(/\s/))
  def aux = doc.select( ".pagination a")?.last()
  if(aux && aux.text().trim().endsWith('>')){
    def next = (url =~ /http:\/\/.*?\//)[0] + aux.attr("href")[1..-1]
    listNames(next, element, cback)
  }
}

listNames("http://www.dicionariodenomesproprios.com.br/nomes-masculinos", "dt a.masculino", { it.each{ el -> println "${el};Masc"} })
listNames("http://www.dicionariodenomesproprios.com.br/nomes-femininos", "dt a.feminino", { it.each{ el -> println "${el};Fem"} })

