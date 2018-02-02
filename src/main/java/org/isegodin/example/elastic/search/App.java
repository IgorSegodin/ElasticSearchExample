package org.isegodin.example.elastic.search;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.isegodin.example.elastic.search.dto.FileDto;
import org.isegodin.example.elastic.search.dto.Identifier;
import org.isegodin.example.elastic.search.dto.PropertyDto;
import org.isegodin.example.elastic.search.dto.TaskDto;
import org.isegodin.example.elastic.search.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

/**
 * https://www.elastic.co/guide/index.html
 *
 * @author i.segodin
 */
public class App {

    public static final String TYPE = "type";

    public static final String USER_INDEX = "user";
    public static final String PROPERTY_INDEX = "property";
    public static final String TASK_INDEX = "task";
    public static final String FILE_INDEX = "file";

    public static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public static final Map<String, Class<? extends Identifier>> indexToClassMap = new HashMap<>();
    public static final Map<Class<? extends Identifier>, String> classToIndexMap = new HashMap<>();
    static {
        indexToClassMap.put(USER_INDEX, UserDto.class);
        indexToClassMap.put(PROPERTY_INDEX, PropertyDto.class);
        indexToClassMap.put(TASK_INDEX, TaskDto.class);
        indexToClassMap.put(FILE_INDEX, FileDto.class);

        for (Map.Entry<String, Class<? extends Identifier>> entry : indexToClassMap.entrySet()) {
            classToIndexMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            )
    );

    public static final FakeDatabase<Long, UserDto> userDatabase = new FakeDatabase<>(Arrays.asList(
            UserDto.builder().id(1L).name("Igor").surname("Segodin").jobTitle("Team Lead").email("igor.segodin@mail.com").telephone("123").build(),
            UserDto.builder().id(2L).name("Anton").surname("Pomanitskiy").jobTitle("Java Developer").email("anton.pomanitskiy@mail.com").telephone("456").build(),
            UserDto.builder().id(3L).name("Vadim").surname("Grechikhin").jobTitle("Java Developer").email("vadim.grechikhin@mail.com").telephone("789").build(),
            UserDto.builder().id(4L).name("Denis").surname("Pustovalov").jobTitle("Java Principal").email("denis.pustovalov@mail.com").telephone("012").build()
    ));

    public static final FakeDatabase<Long, PropertyDto> propertyDatabase = new FakeDatabase<>(Arrays.asList(
            PropertyDto.builder().id(1L).address1("Beetham Tower").address2("301 Deansgate").address3("Manchester M3 4LQ").address4("UK").note("Beetham Tower (also known as the Hilton Tower) is a landmark 47-storey mixed use skyscraper in Manchester, England").build(),
            PropertyDto.builder().id(2L).address1("The Shard").address2("32 London Bridge St").address3("London SE1 9SG").address4("UK").note("The Shard, also referred to as the Shard of Glass,Shard London Bridge and formerly London Bridge Tower, is a 95-storey skyscraper").build(),
            PropertyDto.builder().id(3L).address1("Victoria Tower").address2("Westminster").address3("London SW1P 3JY").address4("UK").note("The Victoria Tower is the square tower at the south-west end of the Palace of Westminster in London, facing south and west onto Black Rod's Garden and Old Palace Yard").build(),
            PropertyDto.builder().id(4L).address1("Heron Tower").address2("110 Bishopsgate").address3("London EC2N 4AY").address4("UK").note("The Heron Tower (officially 110 Bishopsgate) is a commercial skyscraper in London").build()
    ));

    public static final FakeDatabase<Long, TaskDto> taskDatabase = new FakeDatabase<>(Arrays.asList(
            TaskDto.builder().id(1L).code("extinguisher").name("Check extinguisher").property(propertyDatabase.get(1L)).build(),
            TaskDto.builder().id(2L).code("extinguisher").name("Check extinguisher").property(propertyDatabase.get(2L)).build(),
            TaskDto.builder().id(3L).code("extinguisher").name("Check extinguisher").property(propertyDatabase.get(3L)).build(),
            TaskDto.builder().id(4L).code("extinguisher").name("Check extinguisher").property(propertyDatabase.get(4L)).build(),
            TaskDto.builder().id(5L).code("elswitch").name("Inspect electrical switches").property(propertyDatabase.get(2L)).build(),
            TaskDto.builder().id(6L).code("elswitch").name("Inspect electrical switches").property(propertyDatabase.get(3L)).build(),
            TaskDto.builder().id(7L).code("special").name("Special task London").property(propertyDatabase.get(4L)).build()
    ));

    public static final FakeDatabase<String, FileDto> fileDatabase = new FakeDatabase<>(Arrays.asList(
            FileDto.builder().path("/property/1/files/img_01.jpeg").name("img_01.jpeg").size(24L).contentType("image/jpeg").build(),
            FileDto.builder().path("/property/1/task/22/damaged_wall_01.jpeg").name("damaged_wall_01.jpeg").size(45L).contentType("image/jpeg").build(),
            FileDto.builder().path("/property/1/task/22/report.jpeg").name("damaged_wall_01.jpeg").size(45L).contentType("image/jpeg").build(),
            FileDto.builder().path("/property/2/files/img_02.jpeg").name("img_02.jpeg").size(55L).contentType("image/jpeg").build()
    ));

    public static void main(String[] args) throws IOException {
        try {
            createIndexes(userDatabase.listAll());
            createIndexes(propertyDatabase.listAll());
            createIndexes(taskDatabase.listAll());
            createIndexes(fileDatabase.listAll());

            printAll(findBySearchString("london", FILE_INDEX, TASK_INDEX, PROPERTY_INDEX, USER_INDEX));

            System.out.println();
        } finally {
            client.close();
        }
    }

    @SneakyThrows
    private static <T extends Identifier> void createIndexes(Collection<T> items) {
        for (T item : items) {
            String id = String.valueOf(item.getId());
            String index = classToIndexMap.get(item.getClass());
            try {
                client.delete(new DeleteRequest(index).type(TYPE).id(id));
            } catch (Exception e) {
                // suppress
            }

            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer, item);

            IndexResponse indexResponse = client.index(
                    new IndexRequest(index).type(TYPE).id(id)
                            .source(writer.toString(), XContentType.JSON)
//                                .source(
//                                        JsonXContent.contentBuilder()
//                                                .startObject()
//                                                .field("name", u.getName())
//                                                .field("surname", u.getSurname())
//                                        .endObject()
//                                )
            );
        }
    }

    @SneakyThrows
    private static List<Identifier> findBySearchString(String searchString, String index, String... otherIndexes) {
        SearchRequest searchRequest = new SearchRequest();
        String[] indices = toArray(index, otherIndexes);
        searchRequest.indices(indices)
                .types(TYPE);

        SearchSourceBuilder query = SearchSourceBuilder.searchSource().query(
//                QueryBuilders.matchAllQuery()
                QueryBuilders.multiMatchQuery(searchString)
        );

        query.sort(new ScriptSortBuilder(createSortByIndexWeightScript(indices), ScriptSortBuilder.ScriptSortType.NUMBER));
                //query.sort(new FieldSortBuilder("id"));

        searchRequest.source(
                query
        );

//        searchRequest.source().from(0);
//        searchRequest.source().size(5);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();

        List<Identifier> result = new ArrayList<>();
        for (SearchHit hit : hits) {
            result.add(objectMapper.readValue(hit.getSourceAsString(), indexToClassMap.get(hit.getIndex())));
        }
        return result;
    }

    private static void printAll(List<Identifier> items) {
        for (Identifier item : items) {
            System.out.println(item);
        }
    }

    private static String[] toArray(String first, String... other) {
        if (other != null) {
            if (first != null) {
                String[] target = new String[other.length + 1];
                System.arraycopy(other, 0, target, 1, other.length);
                target[0] = first;
                return target;
            } else {
                return other;
            }
        }
        return new String[]{first};
    }

    private static Script createSortByIndexWeightScript(String[] indices) {
        HashMap<String, Object> sortParams = new HashMap<>();

        Map<String, Integer> indexWeightMap = new HashMap<>();
        sortParams.put("indexWeightMap", indexWeightMap);

        for (int i = 0; i < indices.length; i++) {
            indexWeightMap.put(indices[i], i);
        }

        return new Script(ScriptType.INLINE, "painless", "params.indexWeightMap.get(doc._index.value)", sortParams);
    }

}
