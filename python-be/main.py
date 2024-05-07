from flask import Flask, jsonify, request
from sqlalchemy import create_engine, text

app = Flask(__name__)


class ServicePython:
    def __init__(self):
        self.engine = create_engine('mysql://root:password@localhost/dbms_library', isolation_level='READ_UNCOMMITTED')


service = ServicePython()


@app.route('/dirty-write', methods=['POST'])
def dirty_write():
    data = request.get_json()
    id = data.get('id')

    if id is None:
        return jsonify({'error': 'id is required'}), 400

    try:
        with service.engine.connect() as connection:
            query = text("UPDATE artists SET name = 'CCCCC' WHERE id = :id")
            connection.execute(query, {"id": id})
            connection.commit()
        return jsonify({'success': True}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    app.run(debug=True)
