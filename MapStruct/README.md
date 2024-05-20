# I. Giới Thiệu về MapStruct

## 1. MapStruct là gì?

MapStruct là một framework ánh xạ (mapping) cho Java, được thiết kế để tự động hóa quá trình chuyển đổi giữa các đối tượng Java. MapStruct giúp giảm thiểu mã nguồn cần viết thủ công, đồng thời đảm bảo hiệu suất cao và an toàn khi biên dịch.

## 2. Mục Đích Chính của MapStruct

1. **Chuyển Đổi Dữ Liệu**: Chuyển đổi dữ liệu giữa các lớp khác nhau, ví dụ từ DTO (Data Transfer Object) sang Entity và ngược lại.
2. **Giảm Thiểu Mã Nguồn Thủ Công**: Thay vì viết các phương thức chuyển đổi thủ công, bạn chỉ cần định nghĩa một interface với các phương thức chuyển đổi và MapStruct sẽ tự động sinh mã nguồn cần thiết.
3. **Hiệu Suất Cao**: Mã nguồn được sinh ra bởi MapStruct rất hiệu quả vì nó được biên dịch thành mã Java thuần (plain Java), không phụ thuộc vào reflection hay các cơ chế chậm khác.
4. **Đảm Bảo An Toàn Khi Biên Dịch**: Các lỗi ánh xạ có thể được phát hiện sớm trong quá trình biên dịch, giúp giảm thiểu lỗi runtime.

## 3. Các thư viện tương đương

Ngoài MapStruct, còn có một số thư viện và framework khác trong Java giúp thực hiện việc chuyển đổi dữ liệu giữa các đối tượng một cách tự động và hiệu quả. Dưới đây là một số thư viện nổi bật:

1. **ModelMapper**
2. **Orika**
3. **Dozer**
4. **Selma**

Nhìn chung, nếu hiệu suất và an toàn biên dịch là ưu tiên hàng đầu, MapStruct và Selma là lựa chọn tốt.

Nếu cần một thư viện dễ sử dụng và linh hoạt hơn, ModelMapper và Dozer là những lựa chọn khả thi.

Orika là một giải pháp mạnh mẽ nếu cần ánh xạ phức tạp và hiệu suất cao, nhưng nó có thể yêu cầu cấu hình nhiều hơn.

# II. Hướng dẫn nhanh về MapStruct

## 1. Maven

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.0.Beta1</version>
</dependency>
```

Ngoài `dependency`, `annotationProcessorPaths` cũng cần chỉnh sửa

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.0.Beta1</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## 2. Basic Mapping

Về cơ bản, MappeStruct sẽ tự động Map những trường có cùng tên.

Dưới đây là 1 ví dụ

```java
// Lớp nguồn (source) chứa các thuộc tính cần chuyển đổi
public class SimpleSource {
    private String name;
    private String description;
    // getters and setters
}
// Lớp đích (destination) chứa các thuộc tính sau khi chuyển đổi
public class SimpleDestination {
    private String name;
    private String description;
    // getters and setters
}
```

```java
/**
 * Mapper interface sử dụng MapStruct để ánh xạ giữa SimpleSource và SimpleDestination
 * Tại vì MapStruct sẽ tự ánh xạ với những thuộc tính có cùng tên, nên ta không cần làm gì cả
 * @Mapper: Annotation của MapStruct, dùng để xác định đây là một mapper interface.
 * MapStruct sẽ tự động sinh ra mã nguồn cần thiết để thực hiện các chuyển đổi này.
 */
@Mapper
public interface SimpleMapper {
    SimpleDestination sourceToDestination(SimpleSource source);
    SimpleSource destinationToSource(SimpleDestination destination);
}
```

## 2.a Thuộc tính `componentModel` trong `@Mapper`

Trong MapStruct, thuộc tính `componentModel` được sử dụng trong annotation `@Mapper` để xác định cách thức quản lý vòng đời của các instance của mapper.

Tùy thuộc vào giá trị của `componentModel`, mapper có thể được quản lý bởi các container khác nhau như CDI, Spring, hay JSR330.

### Các Giá Trị của componentModel

**default**: (Mặc định) Không sử dụng container nào.

Mapper sẽ được khởi tạo bằng cách sử dụng các phương thức tĩnh của MapStruct.

```java
@Mapper
public interface SimpleMapper {
    SimpleDestination sourceToDestination(SimpleSource source);
    SimpleSource destinationToSource(SimpleDestination destination);
}
```

```java
SimpleMapper mapper = Mappers.getMapper(SimpleMapper.class);
```

**spring**: Sử dụng Spring để quản lý mapper.

Mapper sẽ trở thành một Spring bean và có thể được tiêm (injected) vào các thành phần khác của Spring.

```java
@Mapper(componentModel = "spring")
public interface SimpleMapper {
    SimpleDestination sourceToDestination(SimpleSource source);
    SimpleSource destinationToSource(SimpleDestination destination);
}
```

```java
@Autowired private SimpleMapper mapper;
```

Ngoài ra `componentModel` còn nhận được các giá trị khác như `cdi` hay `jsr330` (tìm hiểu thêm)

## 3. Mapping với thuộc tính khác tên

Ta sẽ dùng `@Mapping` đối với những thuộc tính có tên khác nhau.

Dưới đây là 1 ví dụ

```java
// Lớp nguồn (source) chứa các thuộc tính cần chuyển đổi
public class EmployeeDTO {
    private int employeeId;
    private String employeeName;
    // getters and setters
}
// Lớp đích (destination) chứa các thuộc tính sau khi chuyển đổi
public class Employee {
    private int id;
    private String name;
    // getters and setters
}

```

```java
/**
 * Với những thuộc tính khác tên, ta cần chỉ ra tên ở nguồn là gì, nó sẽ Map tới tên nào ở đích
 * @Mapping: Annotation của MapStruct, dùng để đánh dấu những thuộc tính không thể Map được bằng cách mặc định
 */
@Mapper
public interface EmployeeMapper {

    @Mapping(target = "employeeId", source = "entity.id")
    @Mapping(target = "employeeName", source = "entity.name")
    EmployeeDTO employeeToEmployeeDTO(Employee entity);

    @Mapping(target = "id", source = "dto.employeeId")
    @Mapping(target = "name", source = "dto.employeeName")
    Employee employeeDTOtoEmployee(EmployeeDTO dto);
}
```

## 4. Mapping với thuộc tính dạng Object

```java
public class EmployeeDTO {
    private int employeeId;
    private String employeeName;
    private DivisionDTO division;
    // getters and setters omitted
}
public class Employee {
    private int id;
    private String name;
    private Division division;
    // getters and setters omitted
}
public class Division {
    private int id;
    private String name;
    // default constructor, getters and setters omitted
}
```

```java
/**
 * Với những thuộc tính khác tên, ta cần chỉ ra tên ở nguồn là gì, nó sẽ Map tới tên nào ở đích
 * @Mapping: Annotation của MapStruct, dùng để đánh dấu những thuộc tính không thể Map được bằng cách mặc định
 */
@Mapper
public interface EmployeeMapper {

    @Mapping(target = "employeeId", source = "entity.id")
    @Mapping(target = "employeeName", source = "entity.name")
    EmployeeDTO employeeToEmployeeDTO(Employee entity);

    @Mapping(target = "id", source = "dto.employeeId")
    @Mapping(target = "name", source = "dto.employeeName")
    Employee employeeDTOtoEmployee(EmployeeDTO dto);

    DivisionDTO divisionToDivisionDTO(Division entity);

    Division divisionDTOtoDivision(DivisionDTO dto);
}
```
