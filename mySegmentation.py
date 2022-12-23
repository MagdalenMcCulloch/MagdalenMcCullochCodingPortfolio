#This is a text segmentor I designed for my Computational linguistics class.
#It first segments the text into sentences, then individual words using rules
#outlined below. It works for any text, but here I used The Cat In The Hat
#You can run it by calling it in idle or another python shell.
#I have included the necessary text file, so be sure to download it. 
import re
def mySegmentation():
    text = open('TheCatInTheHat.txt').read()
    #contents = text.read()
    #remove all line breaks: '\n'
    contents = re.sub('(\n)+','',text)
    #text.close()
    contents = re.sub('(\.)+|(\?)+|(\!)+|(\:)','\n',contents)
    contents = re.sub('(-)+',' ',contents)
    contents = re.sub('(,)+|(;)+|(\')+|(\")','',contents)
    sentences = contents.split('\n')
    sentsnum = len(sentences)
    words = contents.split(' ')
    wordsnum = len(words)
    print("Number of sentences in the text:")
    print(sentsnum)
    print("Number of words in the text:")
    print(wordsnum)
    vocab = set(contents)
    vocabnum = len(vocab)
    print("Number of unique words in the text:")
    print(vocabnum)
    #calculates and prints the average sentence length of the text
    average_length = round(wordsnum/sentsnum)
    print("average sentence length: ")
    print(average_length)
    
