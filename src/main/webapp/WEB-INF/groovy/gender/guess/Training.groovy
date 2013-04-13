package gender.guess

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
import org.encog.ml.data.specific.CSVNeuralDataSet;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.persist.EncogDirectoryPersistence;

public class Training {

  public static final String FILENAME = 'WEB-INF/gender.eg'

  public static void saveNetwork(BasicNetwork network){
    EncogDirectoryPersistence.saveObject(new File(FILENAME), network)
  }

  public static BasicNetwork loadNetwork(){
    try{
      EncogDirectoryPersistence.loadObject(new File(FILENAME));
    }catch(org.encog.persist.PersistError pe){
      null
    }
  }

  void train(double minError){
    BasicNetwork network = loadNetwork()
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

    while(train.getError() > minError) {
      train.iteration();
      if(epoch % 10000 == 0) {
        saveNetwork(network)
      }
      epoch++;
    }
    train.finishTraining();
    //test(network, trainingSet)
  }

  void test(BasicNetwork network, MLDataSet trainingSet){
    def diffs = []
    for(MLDataPair pair: trainingSet ) {
      final MLData output = network.compute(pair.getInput());
      diffs << Math.abs(output.getData(0) - pair.ideal.getData(0))
    }
  }

  void startTraining(){
    MLTrain trainedData = train(0.05)
    Encog.getInstance().shutdown();
  }
}
