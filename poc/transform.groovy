/*
 * Script to transform the original database in a normalized
 * CSV data. This has the objective to train and generate 
 * the neural network (see nntraining.groovy)
 */
import java.text.Normalizer
import static lettermap.*

normNames = []
gender = []
new File('names.csv').eachLine{ row ->
  def (name, sex) = row.split(';')
  normNames << nameCode(name)
  gender << ((sex[0] == 'M') ? 1 : 0)
}

while(normNames){
  def idx = new Random().nextInt(normNames.size())
  def name = normNames.remove(idx)
  def aux = name.join(',') + "," + gender.remove(idx)
  println aux.replace('null','0') //+ ','+ (aux[-1] == '1'? '0':'1')
}
