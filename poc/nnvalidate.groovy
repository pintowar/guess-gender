/*
 * Script used to validate the trained neural network (FILENAME)
 */
@Grab(group='org.encog', module='encog-core', version='3.1.0')
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import static lettermap.*

FILENAME = "encogexample.eg";

BasicNetwork load(){
  try{
    EncogDirectoryPersistence.loadObject(new File(FILENAME));
  }catch(org.encog.persist.PersistError pe){
    null
  }
}

def guess(String name){
  def network = load()
  double[] input = nameCode(name) as double[]
  double[] output = [0] as double[]
  network.compute(input, output)
  output[0]
}

def console = System.console()
while((name = console.readLine('Nome: ')) != 'exit'){
  println (guess(name) > new Double(0.5) ? 'M' : 'F')
}
