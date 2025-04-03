package com.example;

import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.RdfNQuad;
import com.apicatalog.rdf.RdfValue;
import com.apicatalog.rdf.RdfLiteral;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.TxnType;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;

public class Titanium2Jena {
    public Titanium2Jena() {}

  /** populate a Jena Dataset from a Titanium Dataset */
  public static DatasetGraph populateDataset( RdfDataset titaniumIn, DatasetGraph jenaToUpdate) {
    //System.out.println( "jenaToUpdate.supportsTransactions " + jenaToUpdate.supportsTransactions() );
	if (jenaToUpdate.supportsTransactions() ) 
        jenaToUpdate.begin(TxnType.WRITE);
    for ( RdfNQuad triple : titaniumIn.toList() ) {
        //System.out.println( triple.getGraphName().toString() + " " + triple.getSubject() + " <" + triple.getPredicate() + "> " + triple.getObject() );
        Node graphNode;
        if( triple.getGraphName().isPresent() )
            graphNode = makeJenaNode( triple.getGraphName().get() );
        else
            graphNode = Quad.tripleInQuad;
        
        Node subNode =  makeJenaNode(triple.getSubject());
        //System.out.println( "Subject node " + subNode); 
        Node predNode = NodeFactory.createURI( triple.getPredicate().getValue());
        //System.out.println( "Predicate node " + predNode); 
        RdfValue obj = triple.getObject();
        Node objNode = makeJenaNode(obj);
        //System.out.println( "Object node " + objNode);

        jenaToUpdate.add(graphNode, subNode, predNode, objNode );
        //System.out.println( "jenaToUpdate.size " + jenaToUpdate.size() );
    };
    if (jenaToUpdate.supportsTransactions() ) 
        jenaToUpdate.commit();
    return jenaToUpdate;
  }
  
  private static Node makeJenaNode(RdfValue obj) {
		Node node;
		if (obj.isIRI())
			node = NodeFactory.createURI(obj.getValue());
		else if (obj.isLiteral()) {
			RdfLiteral literal = obj.asLiteral();
			if (literal.getLanguage().isPresent())
				node = NodeFactory.createLiteralLang(literal.getValue(), literal.getLanguage().get());
			else
				node = NodeFactory.createLiteralDT(literal.getValue(), NodeFactory.getType(literal.getDatatype()));
		} else
			node = NodeFactory.createBlankNode(obj.getValue());
		return node;
	}
}
