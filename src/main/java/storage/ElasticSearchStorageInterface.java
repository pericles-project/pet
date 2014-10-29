//package storage;
//
//
//import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import model.Environment;
//import model.ExtractionResult;
//import model.ExtractionResultCollection;
//import model.Part;
//
//import org.elasticsearch.ElasticSearchException;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.elasticsearch.node.Node;
//import org.elasticsearch.node.NodeBuilder;
//import org.elasticsearch.search.SearchHit;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//
//import configuration.Constants;
//
//
///**
// * Class to start Elasticsearch queries. Can be replaced by another storage
// * backend.
// */
//public class ElasticSearchStorageInterface extends GeneralStorage {
//	private final static String PART_PATH = "file-dependent.Part.path";
//	private final static String PART_PROFILE = "file-dependent.Part.profileUUID";
//	private final static String ENVIRONMENT_PROFILE = "environment.Environment.profileUUID";
//	public static final int MAX_RESULTS = 1000;
//
//	public ElasticSearchStorageInterface() {
//		getClient();
//	}
//
//	private static Client client;
//	private static Node node;
//
//	
//	private Client getClient() {
//		if (client == null) {
//			String map = "{file-dependent: {\n"
//					+ "properties:{"
//					+ "Part: {\n"
//					+ "properties:{"
//					+ "path: { type: \"string\", index : \"not_analyzed\"\n},"
//					+ "profileUUID: { type: \"string\", index : \"not_analyzed\"\n}}"
//					+ "}}}}";
//			String mapEnv = "{environment: {"
//					+ "properties: {"
//					+ "Environment: {"
//					+ "properties: "
//					+ "{"
//					+ "profileUUID: { type: \"string\", index : \"not_analyzed\"\n}}"
//					+ "}}}}";
//			NodeBuilder nb = nodeBuilder();
//			nb.loadConfigSettings(true);
//			nb.settings().put("path.data", Constants.PROJECT_HOME);
//
//			Logger.getLogger("org.elasticsearch").setLevel(Level.WARNING);
//
//			node = nb.local(true).node();
//			client = node.client();
//			if (!client.admin().indices().prepareExists("metadata").get()
//					.isExists()) {
//				client.admin().indices().prepareCreate("metadata")
//						.addMapping("file-dependent", map)
//						.addMapping("environment", mapEnv).get();
//			}
//			client.admin().cluster().prepareHealth().setWaitForGreenStatus()
//					.execute().actionGet();
//		}
//		return client;
//	}
//
//	public void finalize() {
//		node.close();
//		node = null;
//		client = null;
//	}
//
//	
//
//	public void save(String collection,
//			String profileUUID, String type, String path) throws ElasticSearchException{
//		String index = "metadata";
//		getClient().prepareIndex(index, type).setSource(collection)
//				.setRefresh(true).get();
//	}
//
//	public void deleteAllMetadata() {
//		getClient().admin().indices().prepareDelete("metadata").get();
//		client = null;
//		getClient();
//	}
//
//	
//	
//	/**
//	 * Logic to get the data to fill the InformationChangeTable.
//	 * 
//	 * @param environment
//	 * @param moduleName
//	 * @return data to fill the InformationChangeTable
//	 */
//	public String[] getRawResults(ExtractionResultCollection coll) {
//		SearchResponse response = null;
//		if (coll instanceof Part) {
//			Part part = (Part) coll;
//			BoolQueryBuilder query = QueryBuilders.boolQuery()
//					.must(QueryBuilders.termQuery(PART_PATH, part.getPath()))
//					.must(QueryBuilders.termQuery(PART_PROFILE, part.profileUUID));
//			response = searchQuery(query);
//		} else if (coll instanceof Environment) {
//			Environment environment = (Environment) coll;
//			TermQueryBuilder query = QueryBuilders.termQuery(ENVIRONMENT_PROFILE,
//					environment.profileUUID);
//			response = searchQuery(query);
//		} else return null;
//		
//		ArrayList<String> column = new ArrayList<String>();
//		for (SearchHit hit : response.getHits()) {
//			column.add(hit.getSourceAsString());
//		}
//		return column.toArray(new String[0]);
//	}
//
//	
//	/**
//	 * Generic query execution method
//	 * 
//	 * @param query
//	 * @return response
//	 */
//	private SearchResponse searchQuery(QueryBuilder query) {
//		return client.prepareSearch().setQuery(query).setSize(MAX_RESULTS).execute()
//				.actionGet();
//	}
//
//
//	// TODO: this can't be the best way. There must be an easier way to
//	// deserialize back to ExtractionResultCollection
//
//	//
//	// private static ExtractionResultCollection deserializeResults(JsonNode
//	// jsonDocument) {
//	// System.out.println("Document: " + jsonDocument);
//	//
//	// Iterator<JsonNode> jsonChildrenNodes = jsonDocument.elements();
//	// while (jsonChildrenNodes.hasNext()) {
//	// JsonNode child = jsonChildrenNodes.next();
//	// if(child.textValue().equals("extractionResults : ")){
//	// ExtractionResultCollection collection = new ExtractionResultCollection(
//	// "collection");
//	// // collection.addExtractionResult(getResultObject("arrayEntry"));
//	// return collection;
//	// }
//	// } return null;
//	// }
//	// private static ExtractionResult getResultObject(String string) {
//	// ExtractionResult result = new ExtractionResult();
//	// Object resultObject = "TODO";
//	// result.setResults(resultObject);
//	// return result;
//	// }
// }
