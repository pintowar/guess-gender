/*
 * Basic methods for data normalization
 */
import java.text.Normalizer

static def letterCode() { 
  int min = (((int) 'a') - 1)
  int max = (int) 'z'
  [' ':0] + ('a'..'z').collectEntries{ [it, (((int)it) - min)/(max - min)] } 
}
static def codeLetter() { letterCode().collectEntries{ k,v -> [v,k] } }

static def normalize(word){
  def aux = Normalizer.normalize(word.toLowerCase().trim(), Normalizer.Form.NFD);
  aux = aux.replaceAll(/[^\p{ASCII}]/, "")
  (aux.size() > 10) ? (aux[0..4] + aux[-5..-1]) : aux.padLeft(10)
}

static def nameCode(word){
  def map = letterCode()
  normalize(word).collect{ map[it] }
}

static def codeName(code){
  def map = codeLetter()
  code.collect{ map[it] }.join('').trim()
}

