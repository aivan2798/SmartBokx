package com.tutorialspoint.lucene;

import android.util.Log;

import com.blaqbox.smartbocx.db.Note;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
        //this directory will contain the indexes
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));

        //create the indexer
        writer = new IndexWriter(indexDirectory,
                new StandardAnalyzer(Version.LUCENE_36),true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();

        //index file contents
        Field contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file));
        //index file name
        Field fileNameField = new Field(LuceneConstants.FILE_NAME,
                file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED);
        //index file path
        Field filePathField = new Field(LuceneConstants.FILE_PATH,
                file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private Document getDocument(Note note_components) throws IOException {
        Document document = new Document();

        //index file contents
        String note_description  = note_components.note_description;
        Log.i("note_description: ", note_description);

        Field contentField = new Field(LuceneConstants.CONTENTS, note_description.getBytes());
        //index file name
        String note_link  = note_components.note_link;
        Log.i("note_link: ", note_link);
        Field noteNameField = new Field(LuceneConstants.FILE_NAME,
                note_components.note_link,Field.Store.YES,Field.Index.NOT_ANALYZED);
        //index file path

        String note_index  = Integer.toString(note_components.note_id);
        Log.i("note_index: ", note_index);
        Field notePathField = new Field(LuceneConstants.FILE_PATH,
                note_index,Field.Store.YES,Field.Index.NOT_ANALYZED);

        document.add(contentField);
        document.add(noteNameField);
        document.add(notePathField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    private void indexNote(Note note_data) throws IOException {
//        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(note_data);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter)
            throws IOException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if(!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
                    && filter.accept(file)
            ){
                indexFile(file);
            }
        }
        return writer.numDocs();
    }

    public int createNoteIndex(List<Note> notes)
            throws IOException {
        //get all files in the data directory


        for (Note note : notes) {
            {
                indexNote(note);
            }
        }
        return writer.numDocs();
    }
}
