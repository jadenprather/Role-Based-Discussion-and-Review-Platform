/**
 * <h1>TP2 Task 6 – Student Post Tests</h1>
 *
 * <p>This package contains the automated and semi-automated test classes used to verify
 * that the functionality implemented in Tasks 3 (Post class) and 4 (Student Posts GUI)
 * satisfies all <b>Students User Stories</b> defined for the Student Discussion System.
 * These tests were originally developed and executed in Task 5, and they are now formally
 * documented for Task 6 to demonstrate requirement coverage and correctness.</p>
 *
 * <h2>Purpose</h2>
 * <p>The purpose of these tests is to confirm that all CRUD (Create, Read, Update, Delete)
 * operations, validation logic, and GUI interactions in the Student Posts feature behave as
 * expected. Each test method corresponds to one or more user-story requirements, and
 * successful test results show that the implementation meets the intended behavior.</p>
 *
 * <h2>Requirements (R-IDs)</h2>
 * <ul>
 *   <li><b>R-CRUD-01:</b> Valid posts are created, stored, and retrievable.</li>
 *   <li><b>R-CRUD-02:</b> Updates modify attributes while preserving the same post ID.</li>
 *   <li><b>R-CRUD-03:</b> Deleted posts are removed from both the data model and GUI view.</li>
 *   <li><b>R-VAL-01:</b> Title and body validation rules are enforced (non-empty and within limits).</li>
 *   <li><b>R-GUI-01:</b> GUI create action displays success and refreshes the post list view.</li>
 *   <li><b>R-GUI-02:</b> GUI delete action removes the selected post from the view.</li>
 * </ul>
 *
 * <h2>Traceability Matrix</h2>
 * <table border="1">
 *   <tr><th>Requirement ID</th><th>Test File / Method</th></tr>
 *   <tr><td>R-CRUD-01</td><td>PostServiceTest.testCreatePost()</td></tr>
 *   <tr><td>R-CRUD-02</td><td>PostServiceTest.testUpdatePost()</td></tr>
 *   <tr><td>R-CRUD-03</td><td>PostServiceTest.testDeletePost()</td></tr>
 *   <tr><td>R-VAL-01</td><td>PostServiceTest.testValidationRules()</td></tr>
 *   <tr><td>R-GUI-01</td><td>InMemoryPostRepository integration tests</td></tr>
 *   <tr><td>R-GUI-02</td><td>SearchSpec filter and GUI update tests</td></tr>
 * </table>
 *
 * <h2>How to Run</h2>
 * <p>Run JUnit 5 tests in Eclipse (<i>Run As → JUnit Test</i>) or via Maven
 * (<code>mvn test</code>). A fully green test bar confirms that all Student User Story
 * requirements are satisfied.</p>
 *
 * <h2>Interpreting Results</h2>
 * <p>Each passing test indicates that its corresponding requirement has been met.
 * Any failed test highlights a mismatch between expected and actual behavior.
 * Screenshots and code snippets have been appended in <code>StudentPostTests.pdf</code>
 * to provide visual evidence of successful validation.</p>
 */
package studentPosts;
