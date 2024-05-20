
### Kiểu dữ liệu JSON trong PostgreSQL

PostgreSQL có thể lưu trữ dữ liệu dạng JSON. 

Ta có thể lưu trữ cả dữ liệu nguyên thủy và cấu trúc của nó, bao gồm cả Arrays và String

PostgreSQL cung cấp hai loại để lưu trữ dữ liệu JSON: `json` và `jsonb`, chúng chấp nhận giá trị gần như giống hệt nhau nhưng có khác biệt về cách lưu trữ:
 - `json` lưu trữ chính xác dữ liệu đầu vào:
    + Nó lưu kể cả dấu cách trong dữ liệu và thứ tự các key
    + Các hàm xử lý sẽ phải phân tích lại mỗi lần thực thi
    + Các key trùng nhau sẽ được lưu lại toàn bộ (các hàm sẽ thao tác với key cuối cùng)
 - `jsonb` lưu trữ dữ liệu ở dạng nhị phân được phân tách, 
    + Nó khiến lúc `INSERT` chậm hơn 1 chút nhưng nhanh hơn đáng kể lúc thực thi
    + Thứ tự không được lưu lại và không giữ các key trùng lặp, chỉ giá trị cuối được giữ lại
    + `jsonb` còn hỗ trợ lập `INDEX`, đây là 1 lợi thế đáng kể


Nói chung, hầu hết các ứng dụng nên ưu tiên lưu trữ dữ liệu JSON dưới dạng jsonb, trừ khi có những nhu cầu khá chuyên biệt, chẳng hạn như các giả định cũ về thứ tự của các khóa đối tượng.

chuỗi JSON phải được mã hóa bằng UTF8

Các kiểu nguyên thủy JSON và các kiểu PostgreSQL tương ứng
string -> text      : \u0000 is disallowed, as are Unicode escapes representing characters not available in the database encoding
number -> numeric   : NaN và infinity không được phép
boolean -> boolean  : chỉ chấp nhận lowercase `true` và `false`
null -> [không có]  : SQL null là 1 khái niệm khác


### jsonb Containment

Một số trường hợp về toán tử "contains" (@>) khi sử dụng với kiểu dữ liệu JSONB trong PostgreSQL:

```SQL
SELECT '"foo"'::jsonb @> '"foo"'::jsonb;: --Kết quả: true
--Vì cả hai chuỗi JSON đều giống nhau, nên chuỗi bên trái chứa chuỗi bên phải.

SELECT '[1, 2, 3]'::jsonb @> '[1, 3]'::jsonb;: --Kết quả: true
--Mảng bên phải là một phần của mảng bên trái.

SELECT '[1, 2, 3]'::jsonb @> '[3, 1]'::jsonb;: --Kết quả: true
--Thứ tự các phần tử trong mảng không quan trọng, nên mảng bên phải vẫn là một phần của mảng bên trái.

SELECT '[1, 2, 3]'::jsonb @> '[1, 2, 2]'::jsonb;: --Kết quả: true
--Các phần tử trùng lặp không ảnh hưởng đến việc xác định sự chứa của mảng.

SELECT '{"product": "PostgreSQL", "version": 9.4, "jsonb": true}'::jsonb @> '{"version": 9.4}'::jsonb;: --Kết quả: true
--Đối tượng bên phải là một phần của đối tượng bên trái.

SELECT '[1, 2, [1, 3]]'::jsonb @> '[1, 3]'::jsonb;: --Kết quả: false
--Mặc dù mảng bên trái chứa một mảng con [1, 3], nhưng toàn bộ mảng bên phải không phải là một phần của mảng bên trái.

SELECT '[1, 2, [1, 3]]'::jsonb @> '[[1, 3]]'::jsonb;: --Kết quả: true
--Mảng bên phải được chứa trong mảng con [1, 3] trong mảng bên trái.

SELECT '{"foo": {"bar": "baz"}}'::jsonb @> '{"bar": "baz"}'::jsonb;: --Kết quả: false
--Mặc dù đối tượng bên trái có một đối tượng con có key là "bar" và giá trị là "baz", nhưng toàn bộ đối tượng bên phải không phải là một phần của đối tượng bên trái.

SELECT '{"foo": {"bar": "baz"}}'::jsonb @> '{"foo": {}}'::jsonb;: --Kết quả: true
--Mặc dù giá trị của "foo" trong đối tượng bên trái không rỗng, nhưng nó vẫn chứa một đối tượng con trống.
```

### jsonb existence

Toán tử existence (?) trong PostgreSQL JSONB kiểm tra xem một giá trị hoặc một khóa có tồn tại trong JSONB không. 

Nó không chỉ kiểm tra ở cấp độ top-level của JSONB, mà cũng kiểm tra sự tồn tại ở mọi cấp độ lồng nhau của JSONB.

cách sử dụng toán tử existence khi áp dụng cho kiểu dữ liệu JSONB trong PostgreSQL:

```SQL
SELECT '["foo", "bar", "baz"]'::jsonb ? 'bar';: --Kết quả: true
--Chuỗi 'bar' tồn tại như một phần tử trong mảng JSON.

SELECT '{"foo": "bar"}'::jsonb ? 'foo';: --Kết quả: true
--Chuỗi 'foo' tồn tại như một khóa trong đối tượng JSON.

SELECT '{"foo": "bar"}'::jsonb ? 'bar';: --Kết quả: false
--Toán tử "existence" chỉ kiểm tra sự tồn tại của giá trị như một khóa, nó không xem xét giá trị của đối tượng.

SELECT '{"foo": {"bar": "baz"}}'::jsonb ? 'bar';: --Kết quả: false
--Tương tự như trên, "bar" không tồn tại như một khóa trực tiếp trong đối tượng JSON, mà chỉ tồn tại như một khóa trong đối tượng con của "foo".

SELECT '"foo"'::jsonb ? 'foo';: --Kết quả: true
--Chuỗi 'foo' tồn tại như một chuỗi JSON nguyên thủy.
```

### Indexing cho jsonb