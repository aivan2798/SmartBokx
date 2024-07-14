package com.tutorialspoint.lucene;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;
    IndexReader ir;
    public Searcher(String indexDirectoryPath)
            throws IOException {
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));
        for (String indexes: indexDirectory.listAll())
        {
            Log.i("index filename: ",indexes);
        }
        ir = IndexReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(ir);

        int indexSearcher_max = indexSearcher.maxDoc();

        Log.i("max index docs: ", Integer.toString(indexSearcher_max));
        queryParser = new QueryParser(Version.LUCENE_36,
                LuceneConstants.CONTENTS,
                new StandardAnalyzer(Version.LUCENE_36));

    }

    public TopDocs search( String searchQuery)
            throws IOException, ParseException {
        query = queryParser.parse(searchQuery);
        String qp_res = query.toString();
        //queryParser.Query(searchQuery).toString();

        Log.i("log query: ", qp_res);
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        indexSearcher.close();
        ir.close();
    }
}