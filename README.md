# java-consistent-hashing-algorithms

This project collects Java implementations of the most prominent consistent hashing algorithms for non-peer-to-peer contexts.

The implemented algorithms are:
* [1997] __ring hash__ by [D. Karger et al.](https://www.cs.princeton.edu/courses/archive/fall09/cos518/papers/chash.pdf)
* [1998] __rendezvous hash__ by [Thaler and Ravishankar](https://ieeexplore.ieee.org/abstract/document/663936)
* [2014] __jump hash__ by [Lamping and Veach](https://arxiv.org/pdf/1406.2294.pdf)
* [2015] __multi-probe hash__ by [Appleton and O’Reilly](https://arxiv.org/pdf/1505.00062.pdf)
* [2016] __maglev hash__ by [D. E. Eisenbud](https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf)
* [2020] __anchor hash__ by [Gal Mendelson et al.](https://arxiv.org/pdf/1812.09674.pdf)
* [2021] __dx hash__ by [Chaos Dong and Fang Wang](https://arxiv.org/pdf/2107.07930.pdf)
* [2023] __memento hash__ by [M. Coluzzi et al.](https://arxiv.org/pdf/2306.09783.pdf)
* [2024] __jumpback hash__ by [Otmar Ertl](https://arxiv.org/abs/2403.18682)
* [2024] __binomial hash__ by [M. Coluzzi et al.](https://arxiv.org/pdf/2406.19836.pdf)


Each implementation is divided into two classes:
* Each <Algorithm>Engine class (e.g., AnchorEngine) contains an accurate implementation of the algorithm as described in the related paper. These classes do not make any consistency check to keep the performance as close as possible to what was claimed in the related papers.
* Each <Algoritm>Hash class (e.g., AnchorHash) is a wrapper of the related <Algorithm>Engine class allowing every implementation to match the same interface. These classes also perform all the consistency checks needed to grant a safe execution.


## Benchmarks

The project includes a benchmarking tool designed explicitly for consistent hashing algorithms.
The tool allows benchmarking the following metrics in a fair and agnostic way:
- __Memory usage__: the amount of memory the algorithm uses to store its internal structure.
- __Init time__: the time the algorithm requires to initialize its internal structure.
- __Resize time__: the time the algorithm requires to reorganize its internal structure after adding or removing nodes.
- __Lookup time__: the time the algorithm needs to find the node a given key belongs to.
- __Balance__: the ability of the algorithm to spread the keys evenly across the cluster nodes.
- __Resize balance__: the ability of the algorithm to keep its balance after adding or removing nodes.
- __Monotonicity__: the ability of the algorithm to move the minimum amount of resources when the cluster scales.


You can build the tool using `Apache Maven`. It will generate a `jar` file called `consistent-hashing-algorithms-1.0.0-jar-with-dependencies.jar`. You can then run the jar file providing a configuration file to customize your execution.

The format of the configuration file is described in detail in the [`src/main/resources/configs/template.yaml`](src/main/resources/configs/template.yaml) file.
The tool will use the [`src/main/resources/configs/default.yaml`](src/main/resources/configs/default.yaml) file that represents the default configuration if no configuration file is provided.


If the config files are not correctly configured, the tool warns the user and tries to continue the execution.
It will run only the correctly configured benchmarks. If the proceeding is not possible, the tool will return an error.

Refer to the [template.yaml](src/main/resources/configs/template.yaml) file for a complete explanation of the configurations.

Once the configuration file is ready, you can run the benchmarks with the following command:
```sh
$ java -jar consistent-hashing-algorithms-1.0.0-jar-with-dependencies.jar <your-config>.yaml
```


## Add your own consistent hash algorithm

You can add your own consistent hash algorithm by performing a merge request.
The class implementing your algorithm should be called `YourAlgorithm`__`Engine`__.
All the classes subfixed by `Engine` implement the consistent hash algorithms as described in the related papers.

You must implement three more classes to compare your algorithm against the available ones using the benchmark tool. Namely:

- `YourAlgorithm`__`Hash`__: this must implement the [ConsistentHash](src/main/java/ch/supsi/dti/isin/consistenthash/ConsistentHash.java) interface and possibly perform all the consistency checks (that can be avoided in the `YourAlgorithm`__`Engine`__).
- `YourAlgorithm`__`EnginePilot`__: this must implement the [ConsistentHashEnginePilot](src/main/java/ch/supsi/dti/isin/benchmark/adapter/ConsistentHashEnginePilot.java) interface and performs the operations of adding a node, removing a node, and lookup a key by invoking the related methods of the `YourAlgorithmEngine` class in the most efficient way.
- `YourAlgorithm`__`Factory`__: this must implement the [ConsistentHashFactory](src/main/java/ch/supsi/dti/isin/benchmark/adapter/ConsistentHashFactory.java) interface and provides a convenient way to instantiate the algorithm and the other utility classes.
