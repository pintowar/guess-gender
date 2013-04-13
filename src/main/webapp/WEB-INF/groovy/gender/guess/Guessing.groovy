package gender.guess

import java.text.Normalizer
import org.encog.neural.networks.BasicNetwork;
import static gender.guess.Training.*

public class Guessing {

  private static final Map genders = nameMap().asImmutable()

  private static final BasicNetwork network = loadNetwork()

  public static Map letterCode() {
    int min = (((int) 'a') - 1)
    int max = (int) 'z'
    [' ':0] + ('a'..'z').collectEntries{ [it, (((int)it) - min)/(max - min)] }
  }

  public static Map codeLetter() { letterCode().collectEntries{ k,v -> [v,k] } }

  public static String normalize(word){
    def changed = word.endsWith('ã') ? word.replace('ã','an') : word
    def aux = Normalizer.normalize(changed.toLowerCase().trim(), Normalizer.Form.NFD);
    aux = aux.replaceAll(/[^\p{ASCII}]/, "")
    ((aux.size() > 10) ? (aux[0..4] + aux[-5..-1]) : aux.padLeft(10)).toString()
  }

  public static double[] nameCode(word){
    def map = letterCode()
    normalize(word).collect{ map[it] } as double[]
  }

  public static String codeName(code){
    def map = codeLetter()
    code.collect{ map[it] }.join('').trim()
  }

  public static Map nameMap(){
    Map genders = [:]
    new File('WEB-INF/names.csv').eachLine { row ->
      def (k, v) = row.split(';')
      genders.put normalize(k).trim(), v[0]
    }
    genders
  }

  public static char neuralGuess(String name){
    double[] input = nameCode(name) as double[]
    double[] output = [0] as double[]
    network.compute(input, output)
    output[0] > new Double(0.5) ? 'M' : 'F'
  }

  public static char guess(String name){
    Map map = genders
    map[name] ?: neuralGuess(normalize(name.trim().split(' ').first()))
  }

  public static Closure renderMap( Map map ){
    return {
      for ( entry in map ){
        switch( entry.value.getClass() ){
          case Map :
            "${entry.key}" renderMap( entry.value )
          break
          default :
            "${entry.key}"( "${entry.value}" )
          break
        }
      }
    }
  }
}