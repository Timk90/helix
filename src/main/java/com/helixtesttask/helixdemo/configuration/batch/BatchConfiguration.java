package com.helixtesttask.helixdemo.configuration.batch;

import com.helixtesttask.helixdemo.batch.JobCompletionListener;
import com.helixtesttask.helixdemo.batch.UserItemProcessor;
import com.helixtesttask.helixdemo.dao.UserDao;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public ConversionService createConversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(conversionService);
        conversionService.addConverter((Converter<String, LocalDate>) text -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(text, formatter);
        });
        return conversionService;
    }

    @Bean
    public FieldSetMapper<UserDao> testClassRowMapper(ConversionService testConversionService) {
        BeanWrapperFieldSetMapper<UserDao> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setConversionService(testConversionService);
        mapper.setTargetType(UserDao.class);
        return mapper;
    }

    @Bean
    public FlatFileItemReader<UserDao> reader(FieldSetMapper<UserDao> testClassRowMapper) {
        return new FlatFileItemReaderBuilder<UserDao>()
                .name("userItemReader")
                .resource(new ClassPathResource("./db/data.csv"))
                .delimited()
                .names("name", "email", "height", "birthDate", "phoneNumber")
                .fieldSetMapper(testClassRowMapper)
                .build();
    }

    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<UserDao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<UserDao>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO user (name, birthdate, height, last_updated_time, email, phone_number) " +
                        "VALUES (:name, :birthDate, :height, :lastUpdatedTime, :email, :phoneNumber)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(FieldSetMapper<UserDao> testClassRowMapper, JdbcBatchItemWriter<UserDao> writer) {
        return stepBuilderFactory.get("step1")
                .<UserDao, UserDao>chunk(10)
                .reader(reader(testClassRowMapper))
                .processor(processor())
                .writer(writer)
                .build();
    }
}