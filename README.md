# web-search-engine-page-rank-and-hits-

The project purpose is implementing link-based ranking techniques (both evaluation and query parts) such as PageRank and Hits, 
and combine with a term- based ranking function (BM25) on some large dataset. 
Then integrate these techniques with a search engine. 

There are three steps to establish a search engine: crawling, inverted index construction and query processing.

Inverted index construction:
Steps of Generating Inverted Index and calculate PageRank:
1. Program to read the GOV2 data directly from file in sequence.
2. Use the provided parser and jsoup to parse each page to get (word, docID, frequency) tuples. At the same time, extract the hyperlinks from web pages and write the docID and its out links (urls) into file. After parsing a web page, write docID, url and its size into urlTable.
3. When the amount of (word, docID, frequency) tuples in memory is more than a certain number, use quicksort sort (write a Comparable Method to use Arrays sort which provided by java.util.Arrays) to sort this array on docID and word. Then write the sorted array to a file. Here I will merge these 10,000,000 postings into partial inverted list and write the partial inverted list into file.
4. Repeat above steps until all the data is parsed. We will get a bunch of temporary posting files. The temporary files are written as ascii files, because I will use unix sort to merge them in the next step. The data in each temporary file is sorted. I don't use word ID so that I can generate the final inverted index in alphabet order.
5. Use unix sort to merge all the sorted temporary index files into a single file.
6. Parse the merged posting file to reformat it as the final inverted index format, and use the difference of docID and v-byte encoding to compress the inverted list file. Write the lexicon table at the same time.
7. Convert the extracted hyperlink (these links have not been parsed which means there is no inverted list for them) into docID for calculating PageRank scores. Each time convert 3 million links to docID and replace the out links with docID. Iterate this step until all out links have been replaced with docID.
8. Use outlinks file to get in links. Split the out link list into this format: docID (page) docID (citation) and write into inlinks file. Then sort the inlinks file and merge it to get inlinks list.
9. Use MapReduce technique to calculate the PageRank scores until it is convergence and append the scores into urlTable.

query Processing:
Step to Query (PageRank):
1. Upon startup, the query program loads the complete lexicon and URL table data structures from disk into main memory.
2. Create a cache for storing the inverted lists that correspond to query words, which is used for next query when keywords are searched again. Also there is a Hash Map for high frequency words, it is used to record the visited time of query words. When cache is overflow, remove the low frequency word from cache.
3. After input query words, get the inverted list pointer of each word from lexicon. Then open the inverted list file to get the list of the corresponding word and store into cache for next searching.
4. The program is implemented by DAAT query execution technique. If query words contain “or”, then compute bm25 score of pages of all keywords, and return 10 results with the highest scores. If query words contain “and” or just use blank separate query words, in this case, program will return the results that contain all query words. Start with the shortest inverted list, then check all the docID of the shortest inverted list is in other lists or not. If all inverted lists contain this docID, compute its bm25 scores plus PageRank scores and then store into the heap for ranking. Else skip this docID.
5. Finally, this program shows these 10 urls with their scores on the frame. They are web accessible.

Step to Query (Hits):
1. Upon startup, the query program loads the complete lexicon, outlinks, inlinks and URL table data structures from disk into main memory.
2. Create a cache for storing the inverted lists that correspond to query words, which is used for next query when keywords are searched again. Also there is a Hash Map for high frequency words, it is used to record the visited time of query words. When cache is overflow, remove the low frequency word from cache.
3. After input query words, get the inverted list pointer of each word from lexicon. Then open the inverted list file to get the list of the corresponding word and store into cache for next searching.
4. The program is implemented by DAAT query execution technique. If query words contain “or”, then compute bm25 score of pages of all keywords, and return 10 results with the highest scores. If query words contain “and” or just use blank separate query words, in this case, program will return the results that contain all query words. Start with the shortest inverted list, then check all the docID of the shortest inverted list is in other lists or not. If all inverted lists contain this docID, compute its bm25 scores and then store into the heap for ranking. Else skip this docID.
5. Use top 200 results as root set, then get hyperlinks from outlinks and get citation from inlinks to construct a base set.
6. Use base set to get matrix and transpose of matrix, the initial value is 1. Then initialize Hub Score array and Authority Score array with 1/n. Set the max iteration is 100 unless it is convergence, and each iteration will normalize the hub score. Finally return top 10 urls with highest hits scores as results.
