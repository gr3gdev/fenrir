package io.github.gr3gdev.fenrir.jpa;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import lombok.Data;

import javax.sql.DataSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
public class FenrirPersistenceUnitInfo implements PersistenceUnitInfo {

    public static final String JPA_VERSION = "3.1";

    private final String persistenceUnitName;
    private String persistenceProviderClassName;
    private PersistenceUnitTransactionType transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
    private final List<String> managedClassNames;
    private List<String> mappingFileNames = new ArrayList<>();
    private final Properties properties;
    private DataSource jtaDataSource;
    private DataSource nonJtaDataSource;
    private List<ClassTransformer> transformers = new ArrayList<>();
    private List<URL> jarFileUrls;
    private URL persistenceUnitRootUrl;
    private SharedCacheMode sharedCacheMode;
    private ValidationMode validationMode;
    private String persistenceXMLSchemaVersion;
    private ClassLoader classLoader;
    private ClassLoader newTempClassLoader;

    public FenrirPersistenceUnitInfo(String name, List<String> managedClassNames, Properties properties) {
        this.persistenceUnitName = name;
        this.managedClassNames = managedClassNames;
        this.properties = properties;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return false;
    }

    /**
     * @param transformer provider-supplied transformer that the
     *                    container invokes at class-(re)definition time
     */
    @Override
    public void addTransformer(ClassTransformer transformer) {
        this.transformers.add(transformer);
    }
}
