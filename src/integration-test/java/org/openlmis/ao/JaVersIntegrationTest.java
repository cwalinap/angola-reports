package org.openlmis.ao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.ao.reports.dto.CodeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringApplicationConfiguration(Application.class)
public class JaVersIntegrationTest {

  @Autowired
  private Javers javers;

  private static DateTimeZone defaultZone;
  private static final String COMMIT_AUTHOR = "author";

  @BeforeClass
  public static void beforeClass() {
    defaultZone = DateTimeZone.getDefault();
  }

  @After
  public void after() {
    DateTimeZone.setDefault(defaultZone);
  }

  @Test
  public void shouldAlwaysCommitWithUtcTimeZone() throws IOException {

    //given
    CodeDto code = new CodeDto();
    code.setCode("code_1");

    //when
    DateTimeZone.setDefault(DateTimeZone.forID("UTC"));
    javers.commit(COMMIT_AUTHOR, code);

    DateTimeZone.setDefault(DateTimeZone.forID("Africa/Johannesburg"));
    code.setCode("code_2");
    javers.commit(COMMIT_AUTHOR, code);

    //then
    List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byClass(CodeDto.class).build());

    assertEquals(2, snapshots.size());

    LocalDateTime commitTime1 = snapshots.get(0).getCommitMetadata().getCommitDate();
    LocalDateTime commitTime2 = snapshots.get(1).getCommitMetadata().getCommitDate();

    int delta = Math.abs(Seconds.secondsBetween(commitTime1, commitTime2).getSeconds());
    assertTrue(delta < 1);
  }
}
