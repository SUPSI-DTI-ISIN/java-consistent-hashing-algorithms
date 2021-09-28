# java-consistent-hashing-algorithms

This project collects Java implementations of the most popular and best performing consistent hashing algorithms for non-peer-to-peer contexts.

The implemented algorithms are:
* [1997] __ring hash__ by [D. Karger et al.](https://www.cs.princeton.edu/courses/archive/fall09/cos518/papers/chash.pdf)
* [1998] __rendezvous hash__ by [Thaler and Ravishankar](https://ieeexplore.ieee.org/abstract/document/663936)
* [2014] __jump hash__ by [Lamping and Veach](https://arxiv.org/pdf/1406.2294.pdf)
* [2015] __multi-probe hash__ by [Appleton and O’Reilly](https://arxiv.org/pdf/1505.00062.pdf)
* [2016] __maglev hash__ by [D. E. Eisenbud](https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf)
* [2020] __anchor hash__ by [Gal Mendelson et al.](https://arxiv.org/pdf/1812.09674.pdf)
* [2021] __dx hash__ by [Chaos Dong and Fang Wang](https://arxiv.org/pdf/2107.07930.pdf)


Each implementation is divided into two classes:
* Each <Algorithm>Engine class (e.g., AnchorEngine) contains an accurate implementation of the algorithm as described in the related paper. These classes do not make any consistency check to keep the performance as close as possible to what was claimed in the related papers.
* Each <Algoritm>Hash class (e.g., AnchorHash) is a wrapper of the related <Algorithm>Engine class allowing every implementation to match the same interface. These classes also perform all the consistency checks needed to grant a safe execution.
