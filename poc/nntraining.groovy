/*
 * Auxialirity script to train the data from a normalized 
 * database (normnames.csv) and create a neural network
 * defined by FILENAME
 */
@Grab(group='org.encog', module='encog-core', version='3.1.0')
import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.engine.network.activation.ActivationSigmoid;

FILENAME = "encogexample.eg";

def save(BasicNetwork network){
  EncogDirectoryPersistence.saveObject(new File(FILENAME), network)
}

BasicNetwork load(){
  try{
    EncogDirectoryPersistence.loadObject(new File(FILENAME));
  }catch(org.encog.persist.PersistError pe){
    null
  }
}

def reprint(String msg){
  print msg
  print ((['\b']*msg.size()).join('')) + (([' ']*msg.size()).join('')) + ((['\b']*msg.size()).join(''))
}

def train(){
  BasicNetwork network = load()
  if(!network){
    network = new BasicNetwork();
    network.addLayer(new BasicLayer(null, true, 10));
    network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 7));
    network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
    network.getStructure().finalizeStructure();
    network.reset();
  }
  MLDataSet trainingSet = new CSVNeuralDataSet('normnames.csv',10,1,false)

  final MLTrain train = new ResilientPropagation(network, trainingSet);
  // reset if improve is less than 1% over 5 cycles
  //train.addStrategy(new RequiredImprovementStrategy(5));

  int epoch = 1;
  train.iteration();

  while(train.getError() > 0.05) {
    train.iteration();
    if(epoch % 10000 == 0){ println ''; save(network); println 'salvar'}
    reprint("Epoch #" + epoch + " Error:" + train.getError())
    epoch++;
  }
  train.finishTraining();
  test(network, trainingSet)
}

def test(BasicNetwork network, MLDataSet trainingSet){
  println("Neural Network Results:");
  def diffs = []
  for(MLDataPair pair: trainingSet ) {
    final MLData output = network.compute(pair.getInput());
    //println("actual=" + output + ",ideal=" + pair.ideal);
    diffs << Math.abs(output.getData(0) - pair.ideal.getData(0))
  }
  println diffs.sum()/diffs.size()
}

MLTrain trainedData = train()
Encog.getInstance().shutdown();
