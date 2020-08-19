package services;

import elements.Event;
import org.apache.kafka.clients.admin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TopicHandler {

    public Properties props;

    public TopicHandler(Properties props) {
        this.props = props;
    }

    // Apparently the AdminClient has to created new for every call because it times out.
    public List<String> listTopics() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClient.create(props);

        ListTopicsResult topics = adminClient.listTopics();
        Set<String> topicNames = topics.names().get();
        List<String> nameList = new ArrayList<String>(topicNames);

        System.out.println(nameList.toString());
        adminClient.close();

        return nameList;
    }

    public void createTopics(List<String> topicnames) {
        AdminClient adminClient = AdminClient.create(props);

        List<Event> events = new ArrayList<>();

        List<NewTopic> topicList1 = topicnames.stream()
                .map(name -> new NewTopic(name, 1, (short) 1))
                .collect(Collectors.toList());

        adminClient.createTopics(topicList1);
        adminClient.close();
    }

//    public void recusiveDeleteTopics(List<Event> events) {
//        AdminClient adminClient = AdminClient.create(props);
//
//        String[] topicEndings = {"-in", "-out", "-err"};
//        List<String> topicNames = events.stream()
//                .flatMap(event -> Arrays.stream(topicEndings)
//                .map(ending -> event.getName() + ending))
//                .collect(Collectors.toList());
//
//       adminClient.deleteTopics(topicNames);
//       adminClient.close();
//    }

    public void deleteTopics(List<String> topicNames) {
        AdminClient adminClient = AdminClient.create(props);
        adminClient.deleteTopics(topicNames);
    }
}

