package com.example.ArchUnit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ArchitectureTest {

    JavaClasses javaClasses;

    @BeforeEach
    public void beforeEach() {
        javaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS) // 테스트 패키지는 제외
                .importPackages("com.example.ArchUnit");
    }

    @Test
    @DisplayName("Controller 클래스의 이름은 'Controller'로 끝나야 한다.")
    void controllersShouldBeNamedCorrectly() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("서비스 클래스의 이름은 'Service'로 끝나야 한다.")
    void servicesShouldBeNamedCorrectly() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("리포지토리 클래스의 이름은 'Repository'로 끝나야 한다.")
    void repositoriesShouldBeNamedCorrectly() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("컨트롤러는 서비스 클래스에만 의존해야 한다.")
    void controllersShouldOnlyDependOnServices() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..controller..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..service..", "java..", "..controller..")
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("컨트롤러는 필드 인젝션을 사용하지 않아야 한다.")
    void controllersShouldNotUseFieldInjection() {
        ArchRule rule = ArchRuleDefinition.fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().notBeAnnotatedWith(Autowired.class)
                .allowEmptyShould(true);  // 빈 결과 허용


        rule.check(javaClasses);
    }

    @Test
    @DisplayName("클래스는 순환 의존성을 가지면 안 된다.")
    void noCyclicDependencies() {
        ArchRule rule = SlicesRuleDefinition.slices()
                .matching("com.example.ArchUnit.(*)..")
                .should().beFreeOfCycles()
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("서비스 클래스는 컨트롤러 클래스에 접근하지 않아야 한다.")
    void servicesShouldNotAccessControllers() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().onlyAccessClassesThat()
                .resideOutsideOfPackage("..controller..")
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

    @Test
    @DisplayName("예외 클래스는 'Exception'으로 끝나야 한다.")
    void exceptionClassesShouldBeNamedCorrectly() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().areAssignableTo(Exception.class)
                .should().haveSimpleNameEndingWith("Exception")
                .allowEmptyShould(true);  // 빈 결과 허용

        rule.check(javaClasses);
    }

}
