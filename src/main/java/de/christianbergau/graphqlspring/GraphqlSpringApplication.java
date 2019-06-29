package de.christianbergau.graphqlspring;

import de.christianbergau.graphqlspring.resolver.DataResolver;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@SpringBootApplication
public class GraphqlSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlSpringApplication.class, args);
    }

    @Autowired
    DataResolver repository;

    @Bean
    GraphQLSchema schema() {
        URL url = getClass().getResource("./../../../schema.graphqls");
        String sdl = null;

        try {
            sdl = new String(Files.readAllBytes(Paths.get(url.toURI())));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return buildSchema(sdl);
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                        .dataFetcher("bookById", repository.getBookByIdDataFetcher()))
                .type(newTypeWiring("Book")
                        .dataFetcher("author", repository.getAuthorDataFetcher()))
                .build();
    }
}
