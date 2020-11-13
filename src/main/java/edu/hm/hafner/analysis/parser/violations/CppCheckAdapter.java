package edu.hm.hafner.analysis.parser.violations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.hm.hafner.analysis.IssueBuilder;
import edu.hm.hafner.analysis.LineRange;
import edu.hm.hafner.analysis.LineRangeList;
import edu.hm.hafner.analysis.Report;

import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.parsers.CPPCheckParser;

/**
 * Parses CPPCheck files.
 *
 * @author Ullrich Hafner
 */
public class CppCheckAdapter extends AbstractViolationAdapter {
    private static final long serialVersionUID = 2244442395053328008L;

    @Override
    CPPCheckParser createParser() {
        return new CPPCheckParser();
    }

    @Override
    Report convertToReport(final Set<Violation> violations) {
        Map<String, List<Violation>> violationsPerGroup =
                new LinkedHashSet<>(violations).stream().collect(Collectors.groupingBy(Violation::getGroup));

        Report report = new Report();
        IssueBuilder builder = new IssueBuilder();
        for (List<Violation> group : violationsPerGroup.values()) {
            IssueBuilder issueBuilder = updateIssueBuilder(group.get(0), builder);
            LineRangeList lineRanges = new LineRangeList();
            for (int i = 1; i < group.size(); i++) {
                Violation violation = group.get(i);
                lineRanges.add(new LineRange(violation.getStartLine()));
            }
            issueBuilder.setLineRanges(lineRanges);
            report.add(issueBuilder.build());
        }
        builder.dedup();
        return report;
    }
}
